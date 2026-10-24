package com.akshayashokcode.youtubeandroid.features.oauthrequired.subscriptions

import com.akshayashokcode.youtubeandroid.core.model.ResourceId
import com.akshayashokcode.youtubeandroid.core.model.Subscription
import com.akshayashokcode.youtubeandroid.core.model.SubscriptionInsertRequest
import com.akshayashokcode.youtubeandroid.core.model.SubscriptionInsertSnippet
import com.akshayashokcode.youtubeandroid.core.network.YouTubeOAuthApiService
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import com.akshayashokcode.youtubeandroid.core.result.runAppCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SubscriptionsRepository @Inject constructor(
    private val service: YouTubeOAuthApiService,
) {
    fun subscribe(channelId: String): Flow<AppResult<Subscription>> = flow {
        emit(
            runAppCatching {
                require(channelId.isNotBlank()) { "Channel ID is required." }
                service.insertSubscription(
                    body = SubscriptionInsertRequest(
                        snippet = SubscriptionInsertSnippet(
                            resourceId = ResourceId(kind = "youtube#channel", channelId = channelId.trim()),
                        ),
                    ),
                )
            }
        )
    }

    fun unsubscribe(subscriptionId: String): Flow<AppResult<Unit>> = flow {
        emit(
            runAppCatching {
                require(subscriptionId.isNotBlank()) { "Subscription ID is required." }
                service.deleteSubscription(subscriptionId.trim())
            }
        )
    }
}
