package com.SIMATS.binocularvision.utils

import android.content.Context
import android.content.SharedPreferences
import com.SIMATS.binocularvision.api.models.UserProfile

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ID = "user_id"
        private const val KEY_NAME = "user_name"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_PHONE = "user_phone"
    }

    fun saveUser(id: String, name: String, email: String, phone: String) {
        prefs.edit().apply {
            putString(KEY_ID, id)
            putString(KEY_NAME, name)
            putString(KEY_EMAIL, email)
            putString(KEY_PHONE, phone)
            apply()
        }
    }

    fun getUser(): UserProfile? {
        val id = prefs.getString(KEY_ID, null) ?: return null
        val name = prefs.getString(KEY_NAME, "") ?: ""
        val email = prefs.getString(KEY_EMAIL, "") ?: ""
        val phone = prefs.getString(KEY_PHONE, "") ?: ""
        return UserProfile(id, name, email, phone)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getString(KEY_ID, null) != null
    }
}
