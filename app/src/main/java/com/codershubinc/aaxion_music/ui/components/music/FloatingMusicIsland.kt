package com.codershubinc.aaxion_music.ui.components.music

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.SubcomposeAsyncImage
import com.codershubinc.aaxion_music.utils.AaxionServiceInfo
import com.codershubinc.aaxion_music.utils.ServerSelector
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun FloatingMusicIsland(
    serverInfo: AaxionServiceInfo?,
    token: String?,
    onExpand: () -> Unit
) {
    val context = LocalContext.current
    val serverSelector = remember { ServerSelector(context) }
    val musicController = LocalMusicController.current
    val currentTrack = musicController.currentTrack
    
    val activeUrl = serverSelector.getActiveServerUrl(serverInfo)

    AnimatedVisibility(
        visible = currentTrack != null,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = Modifier.fillMaxWidth().zIndex(10f)
    ) {
        if (currentTrack == null) return@AnimatedVisibility
        
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp)
        ) {
            val isWide = maxWidth > 600.dp
            
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .widthIn(max = 800.dp)
                    .fillMaxWidth(if (isWide) 0.8f else 1f)
                    .height(72.dp)
                    .shadow(12.dp, RoundedCornerShape(36.dp))
                    .clip(RoundedCornerShape(36.dp))
                    .clickable { onExpand() },
                color = Color(0xFF18181B).copy(alpha = 0.95f), // Zinc900
                tonalElevation = 8.dp
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp)
                    ) {
                        // --- Album Art ---
                        val imageUrl = if (activeUrl.isNotBlank() && token != null) {
                            val encodedPath = URLEncoder.encode(currentTrack.imagePath, StandardCharsets.UTF_8.toString())
                            val encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8.toString())
                            "$activeUrl/files/thumbnail?path=$encodedPath&tkn=$encodedToken"
                        } else null
                        
                        SubcomposeAsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(24.dp)),
                            contentScale = ContentScale.Crop,
                            error = {
                                Box(Modifier.fillMaxSize().background(Color.DarkGray), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                                }
                            }
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        // --- Title & Artist ---
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentTrack.title,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = currentTrack.artist,
                                color = Color(0xFFA1A1AA), // Zinc400
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        
                        // --- Controls ---
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { musicController.skipPrevious() }) {
                                Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                            
                            IconButton(
                                onClick = { musicController.togglePlayPause() },
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(Color.White, RoundedCornerShape(22.dp))
                            ) {
                                Icon(
                                    imageVector = if (musicController.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = Color.Black,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            
                            IconButton(onClick = { musicController.skipNext() }) {
                                Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                    
                    // --- Progress Bar ---
                    if (musicController.duration > 0) {
                        LinearProgressIndicator(
                            progress = { musicController.playbackPosition.toFloat() / musicController.duration.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth(if (isWide) 0.4f else 0.6f)
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 6.dp)
                                .height(3.dp)
                                .clip(RoundedCornerShape(1.5.dp)),
                            color = Color(0xFF00E5FF),
                            trackColor = Color.White.copy(alpha = 0.1f)
                        )
                    }
                }
            }
        }
    }
}
