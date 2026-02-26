package com.SIMATS.binocularvision.api.models

import com.google.gson.annotations.SerializedName

data class SubmitEyeDataRequest(
    @SerializedName("test_id") val testId: Int,
    @SerializedName("samples") val samples: List<EyeSample>
)
