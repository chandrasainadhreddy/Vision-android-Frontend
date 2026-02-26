package com.SIMATS.binocularvision.api.models

import com.google.gson.annotations.SerializedName

data class RegistrationResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("error")
    val error: String? = null
)
