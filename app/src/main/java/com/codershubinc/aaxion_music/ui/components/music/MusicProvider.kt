package com.codershubinc.aaxion_music.ui.components.music

import androidx.compose.runtime.*

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val albumArtUrl: String? = null,
    val durationMs: Long = 0L
)

class MusicController {
    var currentSong by mutableStateOf<Song?>(null)
        private set
    var isPlaying by mutableStateOf(false)
        private set
    var queue = mutableStateListOf<Song>()
        private set

    fun play(song: Song) {
        currentSong = song
        isPlaying = true
        if (!queue.contains(song)) {
            queue.add(song)
        }
    }

    fun pause() {
        isPlaying = false
    }

    fun resume() {
        if (currentSong != null) {
            isPlaying = true
        }
    }

    fun togglePlayPause() {
        if (isPlaying) pause() else resume()
    }

    fun next() {
        val currentIndex = queue.indexOf(currentSong)
        if (currentIndex != -1 && currentIndex < queue.size - 1) {
            play(queue[currentIndex + 1])
        }
    }

    fun previous() {
        val currentIndex = queue.indexOf(currentSong)
        if (currentIndex > 0) {
            play(queue[currentIndex - 1])
        }
    }
}

val LocalMusicController = staticCompositionLocalOf<MusicController> {
    error("No MusicController provided")
}

@Composable
fun ProvideMusicController(content: @Composable () -> Unit) {
    val musicController = remember { MusicController() }
    CompositionLocalProvider(LocalMusicController provides musicController) {
        content()
    }
}

