package com.akshayashokcode.youtubeandroid.features.oauthrequired.live_streaming

import com.akshayashokcode.youtubeandroid.core.model.LiveBroadcast
import com.akshayashokcode.youtubeandroid.core.model.LiveBroadcastInsertRequest
import com.akshayashokcode.youtubeandroid.core.model.LiveBroadcastInsertSnippet
import com.akshayashokcode.youtubeandroid.core.model.LiveBroadcastInsertStatus
import com.akshayashokcode.youtubeandroid.core.model.LiveBroadcastListResponse
import com.akshayashokcode.youtubeandroid.core.model.LiveStream
import com.akshayashokcode.youtubeandroid.core.model.LiveStreamCdn
import com.akshayashokcode.youtubeandroid.core.model.LiveStreamInsertRequest
import com.akshayashokcode.youtubeandroid.core.model.LiveStreamInsertSnippet
import com.akshayashokcode.youtubeandroid.core.model.LiveStreamListResponse
import com.akshayashokcode.youtubeandroid.core.network.YouTubeOAuthApiService
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import com.akshayashokcode.youtubeandroid.core.result.runAppCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LiveStreamingRepository @Inject constructor(
    private val service: YouTubeOAuthApiService,
) {
    fun getActiveBroadcasts(): Flow<AppResult<LiveBroadcastListResponse>> = flow {
        emit(runAppCatching { service.liveBroadcasts(broadcastStatus = "active") })
    }

    fun createBroadcast(
        title: String,
        scheduledStartTime: String,
        description: String?,
        privacyStatus: String,
    ): Flow<AppResult<LiveBroadcast>> = flow {
        emit(
            runAppCatching {
                require(title.isNotBlank()) { "Broadcast title is required." }
                require(scheduledStartTime.isNotBlank()) { "Scheduled start time is required (ISO 8601)." }
                service.insertLiveBroadcast(
                    body = LiveBroadcastInsertRequest(
                        snippet = LiveBroadcastInsertSnippet(
                            title = title.trim(),
                            scheduledStartTime = scheduledStartTime.trim(),
                            description = description?.trim()?.ifBlank { null },
                        ),
                        status = LiveBroadcastInsertStatus(privacyStatus = privacyStatus),
                    ),
                )
            }
        )
    }

    fun createStream(title: String, resolution: String, frameRate: String): Flow<AppResult<LiveStream>> = flow {
        emit(
            runAppCatching {
                require(title.isNotBlank()) { "Stream title is required." }
                service.insertLiveStream(
                    body = LiveStreamInsertRequest(
                        snippet = LiveStreamInsertSnippet(title = title.trim()),
                        cdn = LiveStreamCdn(
                            ingestionType = "rtmp",
                            resolution = resolution,
                            frameRate = frameRate,
                        ),
                    ),
                )
            }
        )
    }

    fun bindBroadcastToStream(broadcastId: String, streamId: String): Flow<AppResult<LiveBroadcast>> = flow {
        emit(runAppCatching { service.bindLiveBroadcast(id = broadcastId, streamId = streamId) })
    }

    fun transitionBroadcast(broadcastId: String, targetStatus: String): Flow<AppResult<LiveBroadcast>> = flow {
        emit(
            runAppCatching {
                service.transitionLiveBroadcast(
                    broadcastStatus = targetStatus,
                    id = broadcastId,
                )
            }
        )
    }

    fun getMyStreams(): Flow<AppResult<LiveStreamListResponse>> = flow {
        emit(runAppCatching { service.liveStreams() })
    }
}
