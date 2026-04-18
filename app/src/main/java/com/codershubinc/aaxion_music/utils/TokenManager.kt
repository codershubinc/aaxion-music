package com.codershubinc.aaxion_music.utils

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("aaxion_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun clearToken() {
        prefs.edit().remove("auth_token").apply()
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    fun saveServerUrl(url: String) {
        prefs.edit().putString("server_url", url).apply()
    }

    fun getServerUrl(): String? {
        return prefs.getString("server_url", null)
    }

    fun saveConnectionMode(mode: String) {
        prefs.edit().putString("connection_mode", mode).apply()
    }

    fun getConnectionMode(): String {
        return prefs.getString("connection_mode", "Auto") ?: "Auto"
    }
}

