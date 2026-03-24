package com.SIMATS.binocularvision.api

import com.SIMATS.binocularvision.api.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("start_test")
    suspend fun startTest(
        @Body request: StartTestRequest
    ): Response<StartTestResponse>

    @POST("login")
    suspend fun loginUser(
        @Body request: Map<String, String>
    ): Response<LoginResponse>

    @POST("register")
    suspend fun registerUser(
        @Body request: Map<String, String>
    ): Response<RegistrationResponse>

    @POST("forgot-password")
    suspend fun forgotPassword(
        @Body request: Map<String, String>
    ): Response<CommonResponse>

    @POST("reset-password/{token}")
    suspend fun resetPassword(
        @Path("token") token: String,
        @Body request: Map<String, String>
    ): Response<CommonResponse>

    @GET("profile")
    suspend fun getProfile(
        @Query("user_id") userId: String
    ): Response<ProfileResponse>

    @PUT("update_profile")
    suspend fun updateProfile(
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): Response<CommonResponse>

    @POST("logout")
    suspend fun logout(): Response<CommonResponse>

    @POST("upload_eye_data")
    suspend fun uploadEyeData(
        @Body data: Map<String, @JvmSuppressWildcards Any>
    ): Response<CommonResponse>

    @GET("history")
    suspend fun getHistory(
        @Query("user_id") userId: String
    ): Response<HistoryResponse>

    @POST("run_ai")
    suspend fun runAi(
        @Body request: Map<String, Int>
    ): Response<TestResultResponse>

    @GET("get_result")
    suspend fun getResult(
        @Query("test_id") testId: Int
    ): Response<TestResultResponse>

    @GET("home_dashboard")
    suspend fun getHomeDashboard(
        @Query("user_id") userId: String
    ): Response<HomeDashboardResponse>

    @DELETE("delete_test/{test_id}")
    suspend fun deleteTest(
        @Path("test_id") testId: Int,
        @Query("user_id") userId: String
    ): Response<CommonResponse>

    @Multipart
    @POST("api/user/upload-profile-image")
    suspend fun uploadProfileImage(
        @Part("user_id") userId: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<CommonResponse>

    @Multipart
    @POST("update_profile_with_image")
    suspend fun updateProfileWithImage(
        @Part("user_id") userId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<CommonResponse>
}
