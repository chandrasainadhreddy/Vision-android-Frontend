package com.SIMATS.binocularvision.api.models

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("user_id")
    val userId: String? = null
)
