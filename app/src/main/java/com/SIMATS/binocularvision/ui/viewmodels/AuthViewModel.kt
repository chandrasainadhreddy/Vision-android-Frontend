package com.SIMATS.binocularvision.ui.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.SIMATS.binocularvision.api.RetrofitClient
import com.SIMATS.binocularvision.api.models.UserProfile
import com.SIMATS.binocularvision.utils.SessionManager
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
    data class ProfileSuccess(val profile: UserProfile) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)
    private val context = application

    private val _authState = mutableStateOf<AuthState>(AuthState.Idle)
    val authState: State<AuthState> = _authState

    private val _userProfile = mutableStateOf<UserProfile?>(sessionManager.getUser())
    val userProfile: State<UserProfile?> = _userProfile

    fun register(name: String, email: String, phone: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val request = mapOf(
                    "name" to name,
                    "email" to email,
                    "phone" to phone,
                    "password" to password,
                    "confirm_password" to confirmPassword
                )
                val response = RetrofitClient.instance.registerUser(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.status == true) {
                        _authState.value = AuthState.Success(body.message)
                    } else {
                        _authState.value = AuthState.Error(body?.message ?: "Registration failed")
                    }
                } else {
                    _authState.value = AuthState.Error("Server error: ${response.code()}")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val request = mapOf(
                    "email" to email,
                    "password" to password
                )
                val response = RetrofitClient.instance.loginUser(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.status == true) {
                        val userId = body.userId
                        if (userId != null) {
                            fetchProfile(userId)
                        } else {
                             _authState.value = AuthState.Error("Login successful but User ID missing")
                        }
                    } else {
                        _authState.value = AuthState.Error(body?.message ?: "Invalid credentials")
                    }
                } else {
                    _authState.value = AuthState.Error("Server error: ${response.code()}")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val request = mapOf("email" to email)
                val response = RetrofitClient.instance.forgotPassword(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.status == true) {
                        _authState.value = AuthState.Success(body.message ?: "Reset link sent to your email")
                    } else {
                        _authState.value = AuthState.Error(body?.message ?: "Failed to send reset link")
                    }
                } else {
                    _authState.value = AuthState.Error("Server error: ${response.code()}")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    fun resetPassword(token: String, newPassword: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val request = mapOf("password" to newPassword)
                val response = RetrofitClient.instance.resetPassword(token, request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.status == true) {
                        _authState.value = AuthState.Success(body.message ?: "Password updated successfully")
                    } else {
                        _authState.value = AuthState.Error(body?.message ?: "Failed to update password")
                    }
                } else {
                    _authState.value = AuthState.Error("Server error: ${response.code()}")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    fun loginAsGuest() {
        _userProfile.value = UserProfile(
            id = "guest",
            name = "Guest User",
            email = "guest@example.com",
            phone = "N/A"
        )
        _authState.value = AuthState.Success("Logged in as Guest")
    }

    fun fetchProfile(id: String) {
        if (id == "guest") {
            _authState.value = AuthState.ProfileSuccess(_userProfile.value!!)
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = RetrofitClient.instance.getProfile(id)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.status == true && body.profile != null) {
                        val profile = body.profile

                        // Force refresh by adding timestamp to image URL
                        val updatedProfile = if (profile.profileImage != null) {
                            profile.copy(profileImage = "${profile.profileImage}?t=${System.currentTimeMillis()}")
                        } else {
                            profile
                        }

                        _userProfile.value = updatedProfile
                        sessionManager.saveUser(updatedProfile.id, updatedProfile.name, updatedProfile.email, updatedProfile.phone)
                        _authState.value = AuthState.ProfileSuccess(updatedProfile)
                    } else {
                        _authState.value = AuthState.Error(body?.message ?: "Profile not found")
                    }
                } else {
                    _authState.value = AuthState.Error("Server error: ${response.code()}")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    fun uploadProfileImage(uri: Uri) {
        val userId = _userProfile.value?.id ?: return
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val file = getFileFromUri(uri) ?: throw Exception("Failed to get file from Uri")
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                // Key must be "image" to match backend: file = request.files['image']
                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
                val userIdPart = userId.toRequestBody("text/plain".toMediaTypeOrNull())

                val response = RetrofitClient.instance.uploadProfileImage(userIdPart, body)
                if (response.isSuccessful && response.body()?.status == true) {
                    fetchProfile(userId) // Refresh profile after upload
                    _authState.value = AuthState.Success("Profile image updated successfully")
                } else {
                    _authState.value = AuthState.Error(response.body()?.message ?: "Upload failed")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Upload error", e)
                _authState.value = AuthState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.cacheDir, "temp_profile_image.jpg")
        FileOutputStream(file).use { output ->
            inputStream.copyTo(output)
        }
        return file
    }

    fun updateProfile(name: String, email: String, phone: String, imageUri: Uri? = null) {
        val userId = _userProfile.value?.id ?: return
        if (userId == "guest") {
             _authState.value = AuthState.Error("Cannot update guest profile")
             return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val userIdPart = userId.toRequestBody("text/plain".toMediaTypeOrNull())
                val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())
                val phonePart = phone.toRequestBody("text/plain".toMediaTypeOrNull())

                var imagePart: MultipartBody.Part? = null
                imageUri?.let {
                    val file = getFileFromUri(it)
                    if (file != null) {
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
                    }
                }

                val response = RetrofitClient.instance.updateProfileWithImage(
                    userIdPart, namePart, emailPart, phonePart, imagePart
                )

                if (response.isSuccessful && response.body()?.status == true) {
                    _authState.value = AuthState.Success(response.body()?.message ?: "Update successful")
                    fetchProfile(userId)
                } else {
                    _authState.value = AuthState.Error(response.body()?.message ?: "Update failed")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Update error", e)
                _authState.value = AuthState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
        _userProfile.value = null
        _authState.value = AuthState.Idle
    }
    
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
