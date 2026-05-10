package com.akshayashokcode.youtubeandroid.features.apikeyonly.public_playlists

import com.akshayashokcode.youtubeandroid.core.model.PlaylistItemListResponse
import com.akshayashokcode.youtubeandroid.core.model.PlaylistListResponse
import com.akshayashokcode.youtubeandroid.core.network.YouTubeApiService
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import com.akshayashokcode.youtubeandroid.core.result.runAppCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PublicPlaylistsRepository @Inject constructor(
    private val service: YouTubeApiService,
) {
    fun getPublicPlaylistsForChannel(
        channelId: String,
        pageToken: String? = null,
    ): Flow<AppResult<PlaylistListResponse>> = flow {
        emit(runAppCatching { service.playlists(channelId = channelId, pageToken = pageToken) })
    }

    fun getPlaylistItems(
        playlistId: String,
        pageToken: String? = null,
    ): Flow<AppResult<PlaylistItemListResponse>> = flow {
        emit(runAppCatching { service.playlistItems(playlistId = playlistId, pageToken = pageToken) })
    }
}
