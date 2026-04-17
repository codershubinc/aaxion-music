package com.codershubinc.aaxion_music.ui.components.music

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.concurrent.TimeUnit

@Composable
fun ModernAaxionProgressBar(
    playbackPosition: Long,
    duration: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    
    
    LaunchedEffect(playbackPosition, isDragging) {
        if (!isDragging && duration > 0) {
            sliderPosition = playbackPosition.toFloat() / duration.toFloat()
        }
    }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Slider(
            value = sliderPosition,
            onValueChange = { newPosition ->
                isDragging = true
                sliderPosition = newPosition
            },
            onValueChangeFinished = {
                isDragging = false
                // Calculate the exact millisecond to seek to
                val targetPosition = (sliderPosition * duration).toLong()
                onSeek(targetPosition)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color(0xFF00E5FF), // Your Cyan Accent
                inactiveTrackColor = Color(0xFF27272A).copy(alpha = 0.5f) // Zinc 800
            )
        )
        
        // Timestamps below the slider
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatDuration(if (isDragging) (sliderPosition * duration).toLong() else playbackPosition),
                color = Color(0xFFA1A1AA), // Zinc 400
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
            )
            
            Text(
                text = formatDuration(duration),
                color = Color(0xFFA1A1AA), // Zinc 400
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}

/**
 * Helper function to format milliseconds into mm:ss (e.g., 03:45)
 */
private fun formatDuration(durationMs: Long): String {
    if (durationMs < 0) return "00:00"
    
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) - TimeUnit.MINUTES.toSeconds(minutes)
    
    return String.format("%02d:%02d", minutes, seconds)
}