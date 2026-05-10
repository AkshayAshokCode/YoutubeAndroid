package com.akshayashokcode.youtubeandroid.features.apikeyonly.video_details

import com.akshayashokcode.youtubeandroid.core.model.VideoListResponse
import com.akshayashokcode.youtubeandroid.core.network.YouTubeApiService
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import com.akshayashokcode.youtubeandroid.core.result.runAppCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class VideoDetailsRepository @Inject constructor(
    private val service: YouTubeApiService,
) {
    fun getVideoDetails(videoIds: List<String>): Flow<AppResult<VideoListResponse>> = flow {
        emit(
            runAppCatching {
                val ids = videoIds.filter(String::isNotBlank).joinToString(",")
                require(ids.isNotBlank()) { "At least one video ID is required." }
                service.videos(ids = ids)
            },
        )
    }

    fun getPopularVideos(
        regionCode: String = "US",
        videoCategoryId: String? = null,
        pageToken: String? = null,
    ): Flow<AppResult<VideoListResponse>> = flow {
        emit(
            runAppCatching {
                service.videos(
                    chart = "mostPopular",
                    regionCode = regionCode,
                    videoCategoryId = videoCategoryId,
                    pageToken = pageToken,
                )
            },
        )
    }
}
