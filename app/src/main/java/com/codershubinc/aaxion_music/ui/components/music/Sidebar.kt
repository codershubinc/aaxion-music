package com.codershubinc.aaxion_music.ui.components.music

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp

@Composable
fun SidebarWrapper(
    modifier: Modifier = Modifier,
    isMobile: Boolean,
    onCloseDrawer: () -> Unit,
    onLogout: () -> Unit
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
            SidebarContent(isMobile, onCloseDrawer, onLogout)
        }
    }
}

@Composable
fun SidebarContent(isMobile: Boolean, onCloseDrawer: () -> Unit, onLogout: () -> Unit) {
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

        Spacer(modifier = Modifier.weight(1f))

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

