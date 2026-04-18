package com.codershubinc.aaxion_music.ui.components.music

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.codershubinc.aaxion_music.utils.AaxionServiceInfo
import com.codershubinc.aaxion_music.utils.ServerSelector
import com.codershubinc.aaxion_music.utils.rememberCurrentAudioDevice
import com.codershubinc.aaxion_music.utils.music.MusicTrack
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenPlayer(
    serverInfo: AaxionServiceInfo?,
    token: String?,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val serverSelector = remember { ServerSelector(context) }
    val musicController = LocalMusicController.current
    val currentTrack = musicController.currentTrack ?: return

    val activeUrl = serverSelector.getActiveServerUrl(serverInfo)

    val imageUrl = if (activeUrl.isNotBlank() && token != null) {
        val encodedPath = URLEncoder.encode(currentTrack.imagePath, StandardCharsets.UTF_8.toString())
        val encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8.toString())
        "$activeUrl/files/view-image?path=$encodedPath&tkn=$encodedToken"
    } else null

    ModalBottomSheet(
        onDismissRequest = onClose,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.5f)) },
        containerColor = Color.Black,
        scrimColor = Color.Black.copy(alpha = 0.8f),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // --- Premium Blurred Background ---
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(100.dp),
                contentScale = ContentScale.Crop,
                alpha = 0.35f
            )
            
            // Gradient Overlay for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f), Color.Black)
                        )
                    )
            )

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val isWide = maxWidth > 600.dp
                
                if (isWide) {
                    // Responsive Tablet Layout (Side-by-Side)
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 48.dp, vertical = 32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AlbumArtSection(
                            imageUrl = imageUrl,
                            modifier = Modifier.weight(1f).aspectRatio(1f)
                        )
                        
                        Spacer(modifier = Modifier.width(64.dp))
                        
                        Column(
                            modifier = Modifier.weight(1.2f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TrackDetails(currentTrack)
                            Spacer(modifier = Modifier.height(48.dp))
                            PlaybackControls(musicController)
                            Spacer(modifier = Modifier.height(48.dp))
                            AdditionalInfo(currentTrack)
                        }
                    }
                } else {
                    // Responsive Mobile Layout (Vertical)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.weight(0.4f))
                        AlbumArtSection(
                            imageUrl = imageUrl,
                            modifier = Modifier.fillMaxWidth(0.9f).aspectRatio(1f)
                        )
                        Spacer(modifier = Modifier.weight(0.4f))
                        TrackDetails(currentTrack)
                        Spacer(modifier = Modifier.height(32.dp))
                        PlaybackControls(musicController)
                        Spacer(modifier = Modifier.height(32.dp))
                        AdditionalInfo(currentTrack)
                        Spacer(modifier = Modifier.weight(0.8f))
                    }
                }
            }
        }
    }
}

@Composable
private fun AlbumArtSection(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(48.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF18181B))
    ) {
        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            error = {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(100.dp))
                }
            }
        )
    }
}

@Composable
private fun TrackDetails(track: MusicTrack) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = track.artist,
                color = Color(0xFFA1A1AA), // Zinc400
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        IconButton(onClick = { /* Favorite Action */ }) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun PlaybackControls(musicController: MusicController) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ModernAaxionProgressBar(
            playbackPosition = musicController.playbackPosition,
            duration = musicController.duration,
            onSeek = { musicController.seekTo(it) },
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatTime(musicController.playbackPosition), color = Color(0xFFA1A1AA), style = MaterialTheme.typography.bodySmall)
            Text(formatTime(musicController.duration), color = Color(0xFFA1A1AA), style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Shuffle */ }) {
                Icon(Icons.Default.Shuffle, contentDescription = "Shuffle", tint = Color.Gray)
            }
            
            IconButton(onClick = { musicController.skipPrevious() }, modifier = Modifier.size(56.dp)) {
                Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = Color.White, modifier = Modifier.size(40.dp))
            }
            
            FloatingActionButton(
                onClick = { musicController.togglePlayPause() },
                containerColor = Color.White,
                contentColor = Color.Black,
                shape = RoundedCornerShape(50),
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    imageVector = if (musicController.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    modifier = Modifier.size(40.dp)
                )
            }

            IconButton(onClick = { musicController.skipNext() }, modifier = Modifier.size(56.dp)) {
                Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = Color.White, modifier = Modifier.size(40.dp))
            }

            IconButton(onClick = { /* Repeat */ }) {
                Icon(Icons.Default.Repeat, contentDescription = "Repeat", tint = Color.Gray)
            }
        }
    }
}

@Composable
private fun AdditionalInfo(track: MusicTrack) {
    val outputDevice = rememberCurrentAudioDevice()
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Album: ${track.album}",
                color = Color(0xFFA1A1AA),
                style = MaterialTheme.typography.bodyMedium
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    imageVector = when {
                        outputDevice.contains("Bluetooth") -> Icons.Default.Bluetooth
                        outputDevice.contains("Headphones") -> Icons.Default.Headphones
                        else -> Icons.Default.Speaker
                    },
                    contentDescription = null,
                    tint = Color(0xFF00E5FF).copy(alpha = 0.8f),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Playing on $outputDevice",
                    color = Color(0xFF00E5FF).copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            if (track.releaseYear > 0) {
                Text(
                    text = "Released: ${track.releaseYear}",
                    color = Color(0xFF71717A), // Zinc500
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        
        IconButton(onClick = { /* Queue view */ }) {
            Icon(Icons.AutoMirrored.Filled.QueueMusic, contentDescription = "Queue", tint = Color.White)
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
