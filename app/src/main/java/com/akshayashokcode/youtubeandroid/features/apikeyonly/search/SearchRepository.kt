package com.akshayashokcode.youtubeandroid.features.apikeyonly.search

import com.akshayashokcode.youtubeandroid.core.model.SearchListResponse
import com.akshayashokcode.youtubeandroid.core.network.YouTubeApiService
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import com.akshayashokcode.youtubeandroid.core.result.runAppCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val service: YouTubeApiService,
) {
    fun search(
        query: String,
        type: SearchType? = null,
        channelId: String? = null,
        pageToken: String? = null,
    ): Flow<AppResult<SearchListResponse>> = flow {
        emit(
            runAppCatching {
                service.search(
                    query = query,
                    type = type?.apiValue,
                    channelId = channelId,
                    pageToken = pageToken,
                )
            },
        )
    }
}

enum class SearchType(val apiValue: String) {
    Video("video"),
    Channel("channel"),
    Playlist("playlist"),
}
