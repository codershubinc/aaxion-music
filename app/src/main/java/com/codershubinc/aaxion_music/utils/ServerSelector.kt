package com.codershubinc.aaxion_music.utils

import android.content.Context
import java.net.URL

enum class ConnectionMode {
    Auto, LocalOnly, RemoteOnly
}

/**
 * Handles the logic of selecting between a locally discovered server (LAN)
 * and a manually configured backup server (DNS/HTTPS).
 */
class ServerSelector(context: Context) {
    private val tokenManager = TokenManager(context)

    /**
     * Determines the active server base URL based on the current mode and availability.
     */
    fun getActiveServerUrl(discoveredService: AaxionServiceInfo?): String {
        val mode = getConnectionMode()
        val backupUrl = tokenManager.getServerUrl()?.trimEnd('/') ?: ""
        val localUrl = if (discoveredService != null && discoveredService.host != "Unknown") {
            "http://${discoveredService.host}:${discoveredService.port}"
        } else ""

        return when (mode) {
            ConnectionMode.Auto -> {
                // Prioritize Local, fallback to Remote
	            localUrl.ifBlank { backupUrl }
            }
            ConnectionMode.LocalOnly -> localUrl
            ConnectionMode.RemoteOnly -> backupUrl
        }
    }

    fun getConnectionMode(): ConnectionMode {
        return try {
            ConnectionMode.valueOf(tokenManager.getConnectionMode())
        } catch (e: Exception) {
            ConnectionMode.Auto
        }
    }

    fun setConnectionMode(mode: ConnectionMode) {
        tokenManager.saveConnectionMode(mode.name)
    }

    fun hasBackupUrlConfigured(): Boolean {
        return !tokenManager.getServerUrl().isNullOrBlank()
    }

    /**
     * Returns a human-readable source label
     */
    fun getSourceLabel(discoveredService: AaxionServiceInfo?): String {
        val url = getActiveServerUrl(discoveredService)
        val backupUrl = tokenManager.getServerUrl()?.trimEnd('/') ?: ""
        
        return when {
            url.isBlank() -> "Disconnected"
            url == backupUrl -> "Remote (DNS)"
            else -> "Local (LAN)"
        }
    }

    /**
     * Helper to split a URL into host and port
     */
    fun getHostAndPort(url: String): Pair<String, Int> {
        return try {
            val parsedUrl = URL(url)
            val host = parsedUrl.host
            val port = if (parsedUrl.port != -1) parsedUrl.port else if (parsedUrl.protocol == "https") 443 else 80
            Pair(host, port)
        } catch (e: Exception) {
            Pair("", 0)
        }
    }
}
