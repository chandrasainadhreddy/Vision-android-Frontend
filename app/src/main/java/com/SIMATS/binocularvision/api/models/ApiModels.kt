package com.SIMATS.binocularvision.api.models

import com.google.gson.annotations.SerializedName

data class StartTestRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("test_type") val testType: String
)

data class StartTestResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("test_id") val testId: Int? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("error") val error: String? = null
)

data class TestResultResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("test_id") val testId: Int? = null,
    @SerializedName("classification") val classification: String? = null,
    @SerializedName("score") val score: Double? = null,
    @SerializedName("percentage") val percentage: Double? = null,
    @SerializedName("stability") val stability: Double? = null,
    @SerializedName("tracking") val tracking: Double? = null,
    @SerializedName("accuracy") val accuracy: Double? = null,
    @SerializedName("reaction") val reaction: Double? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("error") val error: String? = null
)

data class EyeSample(
    @SerializedName("n") val n: Long,
    @SerializedName("x") val x: Float,
    @SerializedName("y") val y: Float,
    @SerializedName("lx") val lx: Float,
    @SerializedName("ly") val ly: Float,
    @SerializedName("rx") val rx: Float,
    @SerializedName("ry") val ry: Float
)

data class HistoryItemRemote(
    @SerializedName("test_id") val testId: Int,
    @SerializedName("test_type") val testType: String,
    @SerializedName("started_at") val date: String,
    @SerializedName("score") val score: Double?,
    @SerializedName("percentage") val percentage: Double? = null,
    @SerializedName("stability") val stability: Double? = null,
    @SerializedName("tracking") val tracking: Double? = null,
    @SerializedName("accuracy") val accuracy: Double? = null,
    @SerializedName("reaction") val reaction: Double? = null,
    @SerializedName("classification") val classification: String?
)

data class HistoryResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("history") val history: List<HistoryItemRemote>,
    @SerializedName("error") val error: String? = null
)

data class CommonResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("error") val error: String? = null,
    @SerializedName("inserted") val inserted: Int? = null
)

data class HomeDashboardResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("latest_test") val latestTest: HistoryItemRemote? = null,
    @SerializedName("recent_tests") val recentTests: List<HistoryItemRemote> = emptyList(),
    @SerializedName("error") val error: String? = null
)
