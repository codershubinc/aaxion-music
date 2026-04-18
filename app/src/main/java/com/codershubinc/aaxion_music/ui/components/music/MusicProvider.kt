package com.codershubinc.aaxion_music.ui.components.music

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.*
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.codershubinc.aaxion_music.utils.music.MusicTrack
import com.codershubinc.aaxion_music.utils.music.PlaybackService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MusicController(context: Context) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    var controller by mutableStateOf<MediaController?>(null)
        private set

    var currentTrack by mutableStateOf<MusicTrack?>(null)
        private set
    var isPlaying by mutableStateOf(false)
        private set
    var playbackPosition by mutableLongStateOf(0L)
        private set
    var duration by mutableLongStateOf(0L)
        private set

    private var currentPlaylist: List<MusicTrack> = emptyList()
    private var serverUrl: String = ""
    private var token: String? = null

    init {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            controller = controllerFuture?.get()
            controller?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    this@MusicController.isPlaying = isPlaying
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    // This is called when the track changes (either manually or automatically)
                    val track = mediaItem?.localConfiguration?.tag as? MusicTrack
                    if (track != null) {
                        currentTrack = track
                    } else if (mediaItem != null) {
                        // Fallback: try to find the track in our local playlist if the tag is missing
                        val trackId = mediaItem.mediaId.toIntOrNull()
                        currentPlaylist.find { it.id == trackId }?.let {
                            currentTrack = it
                        }
                    }
                    
                    // Reset position and update duration
                    playbackPosition = controller?.currentPosition ?: 0L
                    duration = controller?.duration?.coerceAtLeast(0L) ?: 0L
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        duration = controller?.duration?.coerceAtLeast(0L) ?: 0L
                    } else if (playbackState == Player.STATE_BUFFERING) {
                        // Optional: handle buffering state
                    }
                }

                override fun onPositionDiscontinuity(
                    oldPosition: Player.PositionInfo,
                    newPosition: Player.PositionInfo,
                    reason: Int
                ) {
                    // Force update position on discontinuity (like skipping)
                    playbackPosition = newPosition.positionMs
                }
            })
            
            // Sync initial state if controller is already playing
            this@MusicController.isPlaying = controller?.isPlaying ?: false
            val initialMediaItem = controller?.currentMediaItem
            (initialMediaItem?.localConfiguration?.tag as? MusicTrack)?.let {
                currentTrack = it
            }
            duration = controller?.duration?.coerceAtLeast(0L) ?: 0L
        }, MoreExecutors.directExecutor())
    }

    fun playTrack(track: MusicTrack, playlist: List<MusicTrack>, serverUrl: String, token: String?) {
        this.currentTrack = track
        this.currentPlaylist = playlist
        this.serverUrl = serverUrl
        this.token = token

        val mediaItems = playlist.map { item ->
            val baseUrl = serverUrl.trimEnd('/')
            val encodedToken = URLEncoder.encode(token ?: "", StandardCharsets.UTF_8.toString())
            val url = "$baseUrl/music/stream?id=${item.id}&tkn=$encodedToken"
            val thumbUrl = "$baseUrl/files/thumbnail?path=${URLEncoder.encode(item.imagePath, StandardCharsets.UTF_8.toString())}&tkn=$encodedToken"
            MediaItem.Builder()
                .setMediaId(item.id.toString())
                .setUri(url)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(item.title)
                        .setArtist(item.artist)
                        .setArtworkUri(Uri.parse(thumbUrl))
                        .build()
                )
                .setTag(item)
                .build()
        }
        
        val startIndex = playlist.indexOfFirst { it.id == track.id }.coerceAtLeast(0)
        
        controller?.setMediaItems(mediaItems, startIndex, 0L)
        controller?.prepare()
        controller?.play()
    }

    fun skipNext() {
        if (controller?.hasNextMediaItem() == true) {
            controller?.seekToNextMediaItem()
        } else {
            // Manual fallback if not using native playlist properly
            val currentIndex = currentPlaylist.indexOfFirst { it.id == currentTrack?.id }
            if (currentIndex != -1 && currentIndex < currentPlaylist.size - 1) {
                playTrack(currentPlaylist[currentIndex + 1], currentPlaylist, serverUrl, token)
            }
        }
    }

    fun skipPrevious() {
        if (controller?.hasPreviousMediaItem() == true) {
            controller?.seekToPreviousMediaItem()
        } else {
            // Manual fallback
            val currentIndex = currentPlaylist.indexOfFirst { it.id == currentTrack?.id }
            if (currentIndex != -1 && currentIndex > 0) {
                playTrack(currentPlaylist[currentIndex - 1], currentPlaylist, serverUrl, token)
            }
        }
    }

    fun togglePlayPause() {
        if (isPlaying) {
            controller?.pause()
        } else {
            controller?.play()
        }
    }

    fun seekTo(position: Long) {
        controller?.seekTo(position)
    }

    fun release() {
        MediaController.releaseFuture(controllerFuture!!)
    }

    @Composable
    fun ProgressUpdater() {
        LaunchedEffect(isPlaying) {
            while (isActive && isPlaying) {
                playbackPosition = controller?.currentPosition ?: 0L
                delay(500)
            }
        }
    }
}

val LocalMusicController = staticCompositionLocalOf<MusicController> {
    error("No MusicController provided")
}

@Composable
fun ProvideMusicController(content: @Composable () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val musicController = remember { MusicController(context) }
    
    musicController.ProgressUpdater()

    DisposableEffect(Unit) {
        onDispose {
            // musicController.release() 
            // Better to let it live with the activity or handle properly
        }
    }

    CompositionLocalProvider(LocalMusicController provides musicController) {
        content()
    }
}
