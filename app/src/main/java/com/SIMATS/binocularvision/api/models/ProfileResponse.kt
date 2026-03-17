package com.SIMATS.binocularvision.api.models

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("user") val profile: UserProfile?
)

data class UserProfile(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("profile_image") val profileImage: String? = null
)
