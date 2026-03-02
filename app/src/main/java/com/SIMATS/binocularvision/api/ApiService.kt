package com.SIMATS.binocularvision.api

import com.SIMATS.binocularvision.api.models.*
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

    @POST("delete_test")
    suspend fun deleteTest(
        @Query("test_id") testId: Int
    ): Response<CommonResponse>
}
