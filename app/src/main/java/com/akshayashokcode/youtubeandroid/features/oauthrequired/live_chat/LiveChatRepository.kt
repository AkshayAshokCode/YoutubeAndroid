package com.akshayashokcode.youtubeandroid.features.oauthrequired.live_chat

import com.akshayashokcode.youtubeandroid.core.model.LiveChatMessage
import com.akshayashokcode.youtubeandroid.core.model.LiveChatMessageInsertRequest
import com.akshayashokcode.youtubeandroid.core.model.LiveChatMessageInsertSnippet
import com.akshayashokcode.youtubeandroid.core.model.LiveChatMessageListResponse
import com.akshayashokcode.youtubeandroid.core.model.TextMessageDetails
import com.akshayashokcode.youtubeandroid.core.network.YouTubeOAuthApiService
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import com.akshayashokcode.youtubeandroid.core.result.runAppCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LiveChatRepository @Inject constructor(
    private val service: YouTubeOAuthApiService,
) {
    fun getMessages(liveChatId: String, pageToken: String? = null): Flow<AppResult<LiveChatMessageListResponse>> = flow {
        emit(
            runAppCatching {
                require(liveChatId.isNotBlank()) { "Live chat ID is required." }
                service.liveChatMessages(liveChatId = liveChatId.trim(), pageToken = pageToken)
            }
        )
    }

    fun sendMessage(liveChatId: String, text: String): Flow<AppResult<LiveChatMessage>> = flow {
        emit(
            runAppCatching {
                require(liveChatId.isNotBlank()) { "Live chat ID is required." }
                require(text.isNotBlank()) { "Message text is required." }
                service.insertLiveChatMessage(
                    body = LiveChatMessageInsertRequest(
                        snippet = LiveChatMessageInsertSnippet(
                            liveChatId = liveChatId.trim(),
                            textMessageDetails = TextMessageDetails(messageText = text.trim()),
                        ),
                    ),
                )
            }
        )
    }
}
