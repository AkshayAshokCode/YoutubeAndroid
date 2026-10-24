package com.akshayashokcode.youtubeandroid.features.oauthrequired.manage_playlists

import com.akshayashokcode.youtubeandroid.core.model.Playlist
import com.akshayashokcode.youtubeandroid.core.model.PlaylistInsertRequest
import com.akshayashokcode.youtubeandroid.core.model.PlaylistInsertSnippet
import com.akshayashokcode.youtubeandroid.core.model.PlaylistItem
import com.akshayashokcode.youtubeandroid.core.model.PlaylistItemInsertRequest
import com.akshayashokcode.youtubeandroid.core.model.PlaylistItemInsertSnippet
import com.akshayashokcode.youtubeandroid.core.model.PlaylistStatus
import com.akshayashokcode.youtubeandroid.core.model.ResourceId
import com.akshayashokcode.youtubeandroid.core.network.YouTubeOAuthApiService
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import com.akshayashokcode.youtubeandroid.core.result.runAppCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ManagePlaylistsRepository @Inject constructor(
    private val service: YouTubeOAuthApiService,
) {
    fun createPlaylist(title: String, description: String?, privacyStatus: String): Flow<AppResult<Playlist>> = flow {
        emit(
            runAppCatching {
                require(title.isNotBlank()) { "Playlist title is required." }
                service.insertPlaylist(
                    body = PlaylistInsertRequest(
                        snippet = PlaylistInsertSnippet(
                            title = title.trim(),
                            description = description?.trim()?.ifBlank { null },
                        ),
                        status = PlaylistStatus(privacyStatus = privacyStatus),
                    ),
                )
            }
        )
    }

    fun addVideoToPlaylist(playlistId: String, videoId: String): Flow<AppResult<PlaylistItem>> = flow {
        emit(
            runAppCatching {
                require(playlistId.isNotBlank()) { "Playlist ID is required." }
                require(videoId.isNotBlank()) { "Video ID is required." }
                service.insertPlaylistItem(
                    body = PlaylistItemInsertRequest(
                        snippet = PlaylistItemInsertSnippet(
                            playlistId = playlistId.trim(),
                            resourceId = ResourceId(kind = "youtube#video", videoId = videoId.trim()),
                        ),
                    ),
                )
            }
        )
    }

    fun removePlaylistItem(itemId: String): Flow<AppResult<Unit>> = flow {
        emit(
            runAppCatching {
                require(itemId.isNotBlank()) { "Playlist item ID is required." }
                service.deletePlaylistItem(itemId.trim())
            }
        )
    }
}
