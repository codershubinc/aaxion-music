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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.codershubinc.aaxion_music.ui.components.AaxionMusicWelcomeScreen
import com.codershubinc.aaxion_music.ui.components.LoadingScreen
import com.codershubinc.aaxion_music.ui.components.LoginScreen
import com.codershubinc.aaxion_music.ui.components.music.MusicMainScreen
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
    primary = Color(0xFF00E5FF),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF00E5FF),
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
                val context = LocalContext.current
                val tokenManager = remember { TokenManager(context) }
                
                var currentScreen by remember {
                    mutableStateOf(if (tokenManager.isLoggedIn()) Screen.MainApp else Screen.Loading)
                }
                var discoveredService by remember { mutableStateOf<AaxionServiceInfo?>(null) }
                
                val discovery = remember { NetworkDiscovery(context) }

                LaunchedEffect(Unit) {
                    if (currentScreen == Screen.Loading) {
                        discovery.discoverServices { service ->
                            discoveredService = service
                        }
                        delay(400)
                        currentScreen = Screen.Welcome
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
                                }
                            )
                        }
                        Screen.MainApp -> {
                            MusicMainScreen(
                                onLogout = {
                                    tokenManager.clearToken()
                                    currentScreen = Screen.Welcome
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class Screen {
    Loading, Welcome, Login, MainApp
}
