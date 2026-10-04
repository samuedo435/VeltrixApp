package com.example.veltrixapp.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("veltrix_prefs", Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        prefs.edit().putString("jwt_token", token).apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString("jwt_token", null)
    }

    fun clearSession() {
        prefs.edit().remove("jwt_token").apply()
    }
}