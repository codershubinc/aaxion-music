package com.codershubinc.aaxion_music.ui.components.music

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.codershubinc.aaxion_music.utils.ConnectionMode
import com.codershubinc.aaxion_music.utils.ServerSelector

@Composable
fun SidebarWrapper(
    modifier: Modifier = Modifier,
    isMobile: Boolean,
    onCloseDrawer: () -> Unit,
    onLogout: () -> Unit,
    onOpenSettings: () -> Unit,
    onModeChange: () -> Unit
) {
    Box(
        modifier = modifier.padding(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(24.dp))
                .background(AmoledBlack)
                .padding(16.dp)
        ) {
            SidebarContent(isMobile, onCloseDrawer, onLogout, onOpenSettings, onModeChange)
        }
    }
}

@Composable
fun SidebarContent(isMobile: Boolean, onCloseDrawer: () -> Unit, onLogout: () -> Unit, onOpenSettings: () -> Unit, onModeChange: () -> Unit) {
    val context = LocalContext.current
    val serverSelector = remember { ServerSelector(context) }
    var connectionMode by remember { mutableStateOf(serverSelector.getConnectionMode()) }
    
    var showNoBackupDialog by remember { mutableStateOf(false) }

    var selectedIndex by remember { mutableIntStateOf(0) }
    val menuItems = listOf(
        Pair(Icons.Default.Home, "Home"),
        Pair(Icons.Default.Search, "Search"),
        Pair(Icons.Default.Settings, "Settings")
    )

    Column(modifier = Modifier.fillMaxSize()) {
       Row(
           verticalAlignment = Alignment.Bottom
       ) {
           Text(
               text = "AAXION",
               color = Color.White,
               style = MaterialTheme.typography.headlineMedium,
               modifier = Modifier.padding(bottom = 32.dp, start = 16.dp, top = 16.dp)
           )
           Text(
               text = "music",
               color  = Color.Gray,
               style = MaterialTheme.typography.headlineSmall.copy(
                   fontStyle = FontStyle.Italic,
                   fontFamily = FontFamily.Cursive
               ),
               modifier = Modifier.padding(bottom = 32.dp, start = 16.dp, top = 0.dp)
           )
       }

        menuItems.forEachIndexed { index, item ->
            val isSelected = index == selectedIndex
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) Color(0xFF18181B) else Color.Transparent)
                    .clickable {
                        selectedIndex = index
                        if (item.second == "Settings") {
                            onOpenSettings()
                        }
                        if (isMobile) onCloseDrawer()
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = item.first,
                    contentDescription = item.second,
                    tint = if (isSelected) CyanAccent else Zinc400,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.second,
                    color = if (isSelected) Color.White else Zinc400,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFF27272A))

        // Connection Mode Toggles
        Text(
            "Connection Mode",
            color = Zinc400,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
        )

        ConnectionModeItem(
            icon = Icons.Default.Wifi,
            label = "Auto",
            isSelected = connectionMode == ConnectionMode.Auto,
            onClick = {
                connectionMode = ConnectionMode.Auto
                serverSelector.setConnectionMode(ConnectionMode.Auto)
                onModeChange()
            }
        )
        ConnectionModeItem(
            icon = Icons.Default.Router,
            label = "Local Only",
            isSelected = connectionMode == ConnectionMode.LocalOnly,
            onClick = {
                connectionMode = ConnectionMode.LocalOnly
                serverSelector.setConnectionMode(ConnectionMode.LocalOnly)
                onModeChange()
            }
        )
        ConnectionModeItem(
            icon = Icons.Default.Language,
            label = "Remote Only",
            isSelected = connectionMode == ConnectionMode.RemoteOnly,
            onClick = {
                if (serverSelector.hasBackupUrlConfigured()) {
                    connectionMode = ConnectionMode.RemoteOnly
                    serverSelector.setConnectionMode(ConnectionMode.RemoteOnly)
                    onModeChange()
                } else {
                    showNoBackupDialog = true
                }
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        // No Backup URL Dialog
        if (showNoBackupDialog) {
            AlertDialog(
                onDismissRequest = { showNoBackupDialog = false },
                icon = { Icon(Icons.Default.Language, contentDescription = null, tint = CyanAccent) },
                title = { Text("Backup URL Missing") },
                text = { 
                    Text("You haven't configured a remote server URL yet. Please go to Settings to add one.") 
                },
                confirmButton = {
                    TextButton(onClick = {
                        showNoBackupDialog = false
                        onOpenSettings()
                    }) {
                        Text("Go to Settings", color = CyanAccent)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNoBackupDialog = false }) {
                        Text("Cancel", color = Color.Gray)
                    }
                },
                containerColor = Color(0xFF18181B),
                titleContentColor = Color.White,
                textContentColor = Zinc400
            )
        }

        // Logout Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onLogout() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Logout",
                tint = Color(0xFFEF4444), // Red for logout
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Logout",
                color = Color(0xFFEF4444),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun ConnectionModeItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) CyanAccent.copy(alpha = 0.1f) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) CyanAccent else Zinc400,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            color = if (isSelected) Color.White else Zinc400,
            style = MaterialTheme.typography.bodyMedium
        )
        if (isSelected) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = CyanAccent,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

