package com.SIMATS.binocularvision.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SIMATS.binocularvision.api.RetrofitClient
import com.SIMATS.binocularvision.api.models.HomeDashboardResponse
import kotlinx.coroutines.launch

sealed class HomeState {
    object Loading : HomeState()
    data class Success(val dashboard: HomeDashboardResponse) : HomeState()
    data class Error(val message: String) : HomeState()
}

class HomeViewModel : ViewModel() {
    private val _homeState = mutableStateOf<HomeState>(HomeState.Loading)
    val homeState: State<HomeState> = _homeState

    fun fetchDashboard(userId: String) {
        viewModelScope.launch {
            _homeState.value = HomeState.Loading
            try {
                val response = RetrofitClient.instance.getHomeDashboard(userId)
                if (response.isSuccessful && response.body()?.status == true) {
                    _homeState.value = HomeState.Success(response.body()!!)
                } else {
                    _homeState.value = HomeState.Error(response.body()?.error ?: "Failed to load dashboard")
                }
            } catch (e: Exception) {
                _homeState.value = HomeState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }
}
