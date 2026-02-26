package com.SIMATS.binocularvision.api.models

import com.google.gson.annotations.SerializedName

data class EndTestRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("test_id") val testId: Int
)
