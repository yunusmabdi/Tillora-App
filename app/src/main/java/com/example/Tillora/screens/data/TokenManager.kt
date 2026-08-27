package com.example.Tillora.data

import android.content.Context

class TokenManager(context: Context) {

    private val preferences = context.getSharedPreferences(
        "tillora_auth",
        Context.MODE_PRIVATE
    )

    fun saveToken(token: String) {
        preferences.edit()
            .putString("auth_token", token)
            .apply()
    }

    fun getToken(): String? {
        return preferences.getString("auth_token", null)
    }

    fun clearToken() {
        preferences.edit()
            .remove("auth_token")
            .apply()
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}