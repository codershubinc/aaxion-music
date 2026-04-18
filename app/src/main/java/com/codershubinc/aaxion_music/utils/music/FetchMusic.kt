package com.codershubinc.aaxion_music.utils.music

import com.codershubinc.aaxion_music.utils.HttpClient
import org.json.JSONArray

data class MusicTrack(
    val id: Int,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Int,
    val releaseYear: Int,
    val filePath: String,
    val ytUri: String,
    val imagePath: String,
    val size: Long,
    val createdAt: String
)

object FetchMusic {
    suspend fun fetchAllMusic(baseUrl: String, token: String?): Result<List<MusicTrack>> {
        val url = "${baseUrl.trimEnd('/')}/music/all"
        val result = HttpClient.get(url, token)
        
        return result.mapCatching { response ->
            val jsonArray = JSONArray(response)
            val musicList = mutableListOf<MusicTrack>()
            
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                musicList.add(
                    MusicTrack(
                        id = obj.getInt("id"),
                        title = obj.getString("title"),
                        artist = obj.getString("artist"),
                        album = obj.getString("album"),
                        duration = obj.getInt("duration"),
                        releaseYear = obj.getInt("releaseYear"),
                        filePath = obj.getString("filePath"),
                        ytUri = obj.getString("ytUri"),
                        imagePath = obj.getString("imagePath"),
                        size = obj.getLong("size"),
                        createdAt = obj.getString("createdAt")
                    )
                )
            }
            musicList
        }
    }
}
