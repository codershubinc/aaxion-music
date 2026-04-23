package com.codershubinc.aaxion_music

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.unit.dp
import android.widget.Toast
import com.codershubinc.aaxion_music.ui.components.AaxionMusicWelcomeScreen
import com.codershubinc.aaxion_music.ui.components.LoadingScreen
import com.codershubinc.aaxion_music.ui.components.LoginScreen
import com.codershubinc.aaxion_music.ui.components.SettingsScreen
import com.codershubinc.aaxion_music.ui.components.music.MusicMainScreen
import com.codershubinc.aaxion_music.ui.components.music.ProvideMusicController
import com.codershubinc.aaxion_music.utils.AaxionServiceInfo
import com.codershubinc.aaxion_music.utils.NetworkDiscovery
import com.codershubinc.aaxion_music.utils.TokenManager
import kotlinx.coroutines.delay

val AmoledDarkColorScheme = darkColorScheme(
    background = Color(0xFF000000),
    surface = Color(0xFF18181B),
    surfaceVariant = Color(0xFF27272A),
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFA1A1AA),
    primary = Color(0xFF34393A),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF334548),
    onPrimaryContainer = Color.Black,
    error = Color(0xFFEF4444),
    onError = Color.White,
    outlineVariant = Color(0xFF3F3F46)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = AmoledDarkColorScheme) {
                val view = LocalView.current
                if (!view.isInEditMode) {
                    SideEffect {
                        val window = (view.context as ComponentActivity).window
                        window.statusBarColor = AmoledDarkColorScheme.background.toArgb()
                        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                    }
                }
                ProvideMusicController {
                    val context = LocalContext.current
                    val tokenManager = remember { TokenManager(context) }
                    
                    var currentScreen by remember {
                        mutableStateOf(Screen.Loading)
                    }
                    var discoveredService by remember { mutableStateOf<AaxionServiceInfo?>(null) }
                    var initialCheckDone by remember { mutableStateOf(false) }
                    
                    val discovery = remember { NetworkDiscovery(context) }

                    LaunchedEffect(currentScreen) {
                        if (currentScreen == Screen.Loading) {
                            discoveredService = null
                            discovery.discoverServices(
                                onServiceFound = { service ->
                                    discoveredService = service
                                    Toast.makeText(context, "Server found: ${service.name}", Toast.LENGTH_SHORT).show()
                                },
                                onDiscoveryStarted = {
                                    Toast.makeText(context, "Searching for servers...", Toast.LENGTH_SHORT).show()
                                },
                                onError = { error ->
                                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                },
                                onDiscoveryLost = { serviceName ->
                                    discoveredService = null
                                    Toast.makeText(context, "Server lost: $serviceName", Toast.LENGTH_SHORT).show()
                                }
                            )
                            delay(2000)
                            if (!initialCheckDone) {
                                initialCheckDone = true
                                currentScreen = if (tokenManager.isLoggedIn()) Screen.MainApp else Screen.Welcome
                            } else {
                                currentScreen = if (tokenManager.isLoggedIn()) Screen.MainApp else Screen.Login
                            }
                        }
                    }

                    DisposableEffect(Unit) {
                        onDispose {
                            if (currentScreen != Screen.MainApp) {
                                discovery.stopDiscovery()
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        when (currentScreen) {
                            Screen.Loading -> {
                                LoadingScreen()
                            }
                            Screen.Welcome -> {
                                AaxionMusicWelcomeScreen(
                                    onGetStartedClick = {
                                        currentScreen = Screen.Login
                                    }
                                )
                            }
                            Screen.Login -> {
                                LoginScreen(
                                    serverInfo = discoveredService,
                                    onLoginSuccess = {
                                        currentScreen = Screen.MainApp
                                    },
                                    onRetryDiscovery = {
                                        currentScreen = Screen.Loading
                                    }
                                )
                            }
                            Screen.MainApp -> {
                                MusicMainScreen(
                                    serverInfo = discoveredService,
                                    onLogout = {
                                        tokenManager.clearToken()
                                        currentScreen = Screen.Welcome
                                    },
                                    onRetryDiscovery = {
                                        currentScreen = Screen.Loading
                                    },
                                    onOpenSettings = {
                                        currentScreen = Screen.Settings
                                    }
                                )
                            }
                            Screen.Settings -> {
                                SettingsScreen(
                                    onBack = { currentScreen = Screen.MainApp }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class Screen {
    Loading, Welcome, Login, MainApp, Settings
}
