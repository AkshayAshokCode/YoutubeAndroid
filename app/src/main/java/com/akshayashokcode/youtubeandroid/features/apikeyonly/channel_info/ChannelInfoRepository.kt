package com.akshayashokcode.youtubeandroid.features.apikeyonly.channel_info

import com.akshayashokcode.youtubeandroid.core.model.ChannelListResponse
import com.akshayashokcode.youtubeandroid.core.network.YouTubeApiService
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import com.akshayashokcode.youtubeandroid.core.result.runAppCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ChannelInfoRepository @Inject constructor(
    private val service: YouTubeApiService,
) {
    fun getChannelsById(channelIds: List<String>): Flow<AppResult<ChannelListResponse>> = flow {
        emit(
            runAppCatching {
                val ids = channelIds.filter(String::isNotBlank).joinToString(",")
                require(ids.isNotBlank()) { "At least one channel ID is required." }
                service.channels(ids = ids)
            },
        )
    }

    fun getChannelByUsername(username: String): Flow<AppResult<ChannelListResponse>> = flow {
        emit(runAppCatching { service.channels(username = username) })
    }
}
