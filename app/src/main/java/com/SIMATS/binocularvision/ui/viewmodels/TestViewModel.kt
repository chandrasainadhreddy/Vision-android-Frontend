package com.SIMATS.binocularvision.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.SIMATS.binocularvision.api.RetrofitClient
import com.SIMATS.binocularvision.api.models.EyeSample
import com.SIMATS.binocularvision.api.models.StartTestRequest
import com.SIMATS.binocularvision.api.models.TestResultResponse
import com.SIMATS.binocularvision.api.models.CommonResponse
import com.SIMATS.binocularvision.data.AppDatabase
import com.SIMATS.binocularvision.data.Test
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class TestState {
    object Idle : TestState()
    object Starting : TestState()
    data class Active(val testId: Int) : TestState()
    object Submitting : TestState()
    object Analyzing : TestState()
    data class Finished(val result: TestResultResponse) : TestState()
    data class Error(val message: String) : TestState()
}

class TestViewModel(application: Application) : AndroidViewModel(application) {
    private val testDao = AppDatabase.getDatabase(application).testDao()

    private val _testState = mutableStateOf<TestState>(TestState.Idle)
    val testState: State<TestState> = _testState

    private val _filterTestType = mutableStateOf("All Types")
    val filterTestType: State<String> = _filterTestType

    private val _filterDateRange = mutableStateOf("All Time")
    val filterDateRange: State<String> = _filterDateRange

    private val _history = mutableStateOf<List<com.SIMATS.binocularvision.api.models.HistoryItemRemote>>(emptyList())
    val history: State<List<com.SIMATS.binocularvision.api.models.HistoryItemRemote>> = _history

    private val _samples = mutableListOf<EyeSample>()
    var testId: Int? = null
        private set
    var currentUserId: String? = null
        private set

    private var historyObservationJob: Job? = null

    fun startTest(userId: String, testType: String) {
        viewModelScope.launch {
            _testState.value = TestState.Starting
            testId = null
            _samples.clear()
            
            if (currentUserId != userId) {
                currentUserId = userId
                observeLocalHistory(userId)
            }

            try {
                val backendType = when(testType) {
                    "Fixation Test" -> "ran"
                    "Quick Screening" -> "vrg"
                    "Full Assessment" -> "pur"
                    else -> testType.lowercase()
                }
                val request = StartTestRequest(userId, backendType)
                val response = RetrofitClient.instance.startTest(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.status == true && body.testId != null) {
                        testId = body.testId
                        _testState.value = TestState.Active(body.testId)
                    } else {
                        _testState.value = TestState.Error(body?.error ?: body?.message ?: "Failed to start test")
                    }
                } else {
                    _testState.value = TestState.Error("Server error: ${response.code()}")
                }
            } catch (e: Exception) {
                _testState.value = TestState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    private fun observeLocalHistory(userId: String) {
        historyObservationJob?.cancel()
        historyObservationJob = viewModelScope.launch {
            testDao.getTestsByUserId(userId).collectLatest { localTests ->
                _history.value = localTests.map {
                    com.SIMATS.binocularvision.api.models.HistoryItemRemote(
                        testId = it.id,
                        testType = it.testType,
                        score = it.score,
                        percentage = it.percentage,
                        stability = it.stability,
                        tracking = it.tracking,
                        accuracy = it.accuracy,
                        reaction = it.reaction,
                        classification = it.classification,
                        date = it.date
                    )
                }
            }
        }
    }

    fun addSample(n: Long, x: Float, y: Float, lx: Float, ly: Float, rx: Float, ry: Float) {
        if (_testState.value is TestState.Active) {
            _samples.add(EyeSample(n, x, y, lx, ly, rx, ry))
        }
    }

    fun submitData() {
        val currentTestId = this.testId ?: return
        val userId = currentUserId ?: ""
        
        viewModelScope.launch {
            _testState.value = TestState.Submitting
            try {
                val uploadData = mapOf("test_id" to currentTestId, "samples" to _samples)
                val uploadResponse = RetrofitClient.instance.uploadEyeData(uploadData)
                
                if (uploadResponse.isSuccessful && uploadResponse.body()?.status == true) {
                    _testState.value = TestState.Analyzing
                    val aiResponse = RetrofitClient.instance.runAi(mapOf("test_id" to currentTestId))
                    
                    if (aiResponse.isSuccessful && aiResponse.body()?.status == true) {
                        // AI triggered successfully, now fetch the final results
                        fetchResult(currentTestId)
                    } else {
                        val errorJson = aiResponse.errorBody()?.string()
                        val errorMessage = try {
                            Gson().fromJson(errorJson, TestResultResponse::class.java).message
                                ?: Gson().fromJson(errorJson, TestResultResponse::class.java).error
                        } catch (e: Exception) { null }
                        
                        _testState.value = TestState.Error(errorMessage ?: "AI Trigger Error: ${aiResponse.code()}")
                    }
                } else {
                    val errorJson = uploadResponse.errorBody()?.string()
                    val errorMessage = try {
                        Gson().fromJson(errorJson, CommonResponse::class.java).error
                    } catch (e: Exception) { null }
                    _testState.value = TestState.Error(errorMessage ?: "Upload Error: ${uploadResponse.code()}")
                }
            } catch (e: Exception) {
                _testState.value = TestState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    fun fetchResult(testId: Int) {
        val userId = currentUserId ?: ""
        viewModelScope.launch {
            _testState.value = TestState.Analyzing
            try {
                val response = RetrofitClient.instance.getResult(testId)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.status == true) {
                        val score = body.score ?: 0.0
                        val classification = body.classification ?: "Normal"
                        val percentage = body.percentage ?: 0.0
                        val stability = body.stability ?: 0.0
                        val tracking = body.tracking ?: 0.0
                        val accuracy = body.accuracy ?: 0.0
                        val reaction = body.reaction ?: 0.0

                        val newTest = Test(
                            userId = userId,
                            testType = "Assessment",
                            score = score,
                            percentage = percentage,
                            stability = stability,
                            tracking = tracking,
                            accuracy = accuracy,
                            reaction = reaction,
                            classification = classification,
                            date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                        )
                        testDao.insertTest(newTest)

                        _testState.value = TestState.Finished(body)
                        fetchHistory(userId)
                    } else {
                        _testState.value = TestState.Error(body?.message ?: "Result not ready")
                    }
                } else {
                    _testState.value = TestState.Error("Failed to fetch result: ${response.code()}")
                }
            } catch (e: Exception) {
                _testState.value = TestState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    fun fetchHistory(userId: String) {
        if (currentUserId != userId) {
            currentUserId = userId
            observeLocalHistory(userId)
        }
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getHistory(userId)
                if (response.isSuccessful && response.body()?.status == true) {
                    val remoteHistory = response.body()?.history ?: emptyList()
                    val localTests = remoteHistory.map {
                        Test(
                            userId = userId,
                            testType = it.testType,
                            score = it.score ?: 0.0,
                            percentage = it.percentage ?: 0.0,
                            stability = it.stability ?: 0.0,
                            tracking = it.tracking ?: 0.0,
                            accuracy = it.accuracy ?: 0.0,
                            reaction = it.reaction ?: 0.0,
                            classification = it.classification ?: "Normal",
                            date = it.date
                        )
                    }
                    testDao.deleteTestsByUserId(userId)
                    testDao.insertTests(localTests)
                }
            } catch (e: Exception) {
                Log.e("TestViewModel", "Error fetching history", e)
            }
        }
    }

    val filteredHistory: State<List<com.SIMATS.binocularvision.api.models.HistoryItemRemote>> = derivedStateOf {
        val allHistory = _history.value
        val type = _filterTestType.value
        val range = _filterDateRange.value
        var filtered = allHistory
        if (type != "All Types") {
            filtered = filtered.filter { item ->
                val displayType = when(item.testType.lowercase()) {
                    "ran" -> "Fixation Test"
                    "vrg" -> "Quick Screening"
                    "pur" -> "Full Assessment"
                    else -> item.testType
                }
                displayType == type
            }
        }
        if (range != "All Time") {
            val now = System.currentTimeMillis()
            val dayMillis = 24 * 60 * 60 * 1000L
            filtered = filtered.filter { item ->
                try {
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val date = sdf.parse(item.date)
                    val diff = now - (date?.time ?: 0L)
                    when (range) {
                        "Today" -> diff < dayMillis
                        "This Week" -> diff < 7 * dayMillis
                        "This Month" -> diff < 30 * dayMillis
                        else -> true
                    }
                } catch (e: Exception) { true }
            }
        }
        filtered
    }

    fun applyFilters(dateRange: String, testType: String) {
        _filterDateRange.value = dateRange
        _filterTestType.value = testType
    }

    fun reset() {
        _testState.value = TestState.Idle
        testId = null
        _samples.clear()
    }
}
