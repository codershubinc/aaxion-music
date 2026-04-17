package com.codershubinc.aaxion_music.ui.components.music

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

val AmoledBlack = Color(0xFF000000)
val Zinc900 = Color(0xFF18181B)
val Zinc800 = Color(0xFF27272A)
val Zinc400 = Color(0xFFA1A1AA)
val CyanAccent = Color(0xFF00E5FF)

@Composable
fun MusicMainScreen(onLogout: () -> Unit = {}) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack) // Root background
    ) {
        val isTablet = maxWidth >= 600.dp
        
        if (isTablet) {
            // Tablet/Desktop Layout: Fixed Sidebar + Main Content
            Row(modifier = Modifier.fillMaxSize()) {
                SidebarWrapper(
                    modifier = Modifier
                        .width(280.dp)
                        .fillMaxHeight(),
                    isMobile = false,
                    onCloseDrawer = {},
                    onLogout = onLogout
                )
                
                MainContentWrapper(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    onOpenDrawer = {},
                    showHamburger = false
                )
            }
        } else {
            // Mobile Layout: Modal Drawer
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            
            ModalNavigationDrawer(
                drawerState = drawerState,
                scrimColor = Color.Black.copy(alpha = 0.6f),
                drawerContent = {
                    ModalDrawerSheet(
                        drawerContainerColor = Color.Transparent,
                        drawerContentColor = Color.Transparent,
                        modifier = Modifier.width(300.dp)
                    ) {
                        SidebarWrapper(
                            modifier = Modifier.fillMaxSize(),
                            isMobile = true,
                            onCloseDrawer = {
                                scope.launch { drawerState.close() }
                            },
                            onLogout = onLogout
                        )
                    }
                }
            ) {
                MainContentWrapper(
                    modifier = Modifier.fillMaxSize(),
                    onOpenDrawer = {
                        scope.launch { drawerState.open() }
                    },
                    showHamburger = true
                )
            }
        }
    }
}

@Composable
fun MainContentWrapper(
    modifier: Modifier = Modifier,
    onOpenDrawer: () -> Unit,
    showHamburger: Boolean = false
) {
    Box(
        modifier = modifier.padding(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(24.dp))
                .background(Zinc900)
        ) {
            MainMusicContent(onOpenDrawer, showHamburger)
        }
    }
}

@Composable
fun MainMusicContent(onOpenDrawer: () -> Unit, showHamburger: Boolean) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (showHamburger) {
            IconButton(
                onClick = onOpenDrawer,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open Sidebar",
                    tint = Color.White
                )
            }
        } else {
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Main Music Player Content",
                color = Zinc400,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}
