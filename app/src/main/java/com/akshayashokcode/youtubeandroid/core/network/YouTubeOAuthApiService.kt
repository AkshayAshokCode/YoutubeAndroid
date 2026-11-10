package com.akshayashokcode.youtubeandroid.core.network

import com.akshayashokcode.youtubeandroid.core.model.CaptionListResponse
import com.akshayashokcode.youtubeandroid.core.model.CommentThread
import com.akshayashokcode.youtubeandroid.core.model.CommentThreadInsertRequest
import com.akshayashokcode.youtubeandroid.core.model.LiveBroadcast
import com.akshayashokcode.youtubeandroid.core.model.LiveBroadcastInsertRequest
import com.akshayashokcode.youtubeandroid.core.model.LiveBroadcastListResponse
import com.akshayashokcode.youtubeandroid.core.model.LiveChatMessage
import com.akshayashokcode.youtubeandroid.core.model.LiveChatMessageInsertRequest
import com.akshayashokcode.youtubeandroid.core.model.LiveChatMessageListResponse
import com.akshayashokcode.youtubeandroid.core.model.LiveStream
import com.akshayashokcode.youtubeandroid.core.model.LiveStreamInsertRequest
import com.akshayashokcode.youtubeandroid.core.model.LiveStreamListResponse
import com.akshayashokcode.youtubeandroid.core.model.Playlist
import com.akshayashokcode.youtubeandroid.core.model.PlaylistInsertRequest
import com.akshayashokcode.youtubeandroid.core.model.PlaylistItem
import com.akshayashokcode.youtubeandroid.core.model.PlaylistItemInsertRequest
import com.akshayashokcode.youtubeandroid.core.model.Subscription
import com.akshayashokcode.youtubeandroid.core.model.SubscriptionInsertRequest
import com.akshayashokcode.youtubeandroid.core.model.Video
import com.akshayashokcode.youtubeandroid.core.model.VideoInsertRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Url

interface YouTubeOAuthApiService {

    // ── Comments ──────────────────────────────────────────────────────────────

    @POST("youtube/v3/commentThreads")
    suspend fun insertCommentThread(
        @Query("part") part: String = "snippet",
        @Body body: CommentThreadInsertRequest,
    ): CommentThread

    @DELETE("youtube/v3/comments")
    suspend fun deleteComment(
        @Query("id") id: String,
    )

    // ── Playlists (write) ─────────────────────────────────────────────────────

    @POST("youtube/v3/playlists")
    suspend fun insertPlaylist(
        @Query("part") part: String = "snippet,status",
        @Body body: PlaylistInsertRequest,
    ): Playlist

    @DELETE("youtube/v3/playlists")
    suspend fun deletePlaylist(
        @Query("id") id: String,
    )

    @POST("youtube/v3/playlistItems")
    suspend fun insertPlaylistItem(
        @Query("part") part: String = "snippet",
        @Body body: PlaylistItemInsertRequest,
    ): PlaylistItem

    @DELETE("youtube/v3/playlistItems")
    suspend fun deletePlaylistItem(
        @Query("id") id: String,
    )

    // ── Subscriptions ─────────────────────────────────────────────────────────

    @POST("youtube/v3/subscriptions")
    suspend fun insertSubscription(
        @Query("part") part: String = "snippet",
        @Body body: SubscriptionInsertRequest,
    ): Subscription

    @DELETE("youtube/v3/subscriptions")
    suspend fun deleteSubscription(
        @Query("id") id: String,
    )

    // ── Captions ──────────────────────────────────────────────────────────────

    @GET("youtube/v3/captions")
    suspend fun listCaptions(
        @Query("videoId") videoId: String,
        @Query("part") part: String = "snippet",
    ): CaptionListResponse

    @Multipart
    @POST("upload/youtube/v3/captions")
    suspend fun uploadCaption(
        @Query("part") part: String = "snippet",
        @Query("videoId") videoId: String,
        @Query("language") language: String,
        @Query("name") name: String,
        @Part file: MultipartBody.Part,
    )

    @DELETE("youtube/v3/captions")
    suspend fun deleteCaption(
        @Query("id") id: String,
    )

    // ── Video upload (resumable protocol) ─────────────────────────────────────
    // https://developers.google.com/youtube/v3/guides/using_resumable_upload_protocol

    /** Step 1: send metadata. The upload session URI comes back in the Location header. */
    @POST("upload/youtube/v3/videos?uploadType=resumable")
    suspend fun startVideoUpload(
        @Query("part") part: String = "snippet,status",
        @Header("X-Upload-Content-Length") contentLength: Long,
        @Header("X-Upload-Content-Type") contentType: String,
        @Body metadata: VideoInsertRequest,
    ): Response<Unit>

    /**
     * Step 2: send one chunk to the session URI with `Content-Range: bytes start-end/total`.
     * 308 = chunk stored, send the next one (Range header says how much the server has);
     * 200/201 = upload finished, body is the Video.
     * An empty chunk with an unknown range asks how much the server has (see VideoUploadRepository).
     */
    @PUT
    suspend fun uploadVideoChunk(
        @Url sessionUri: String,
        @Header("Content-Range") contentRange: String,
        @Body chunk: RequestBody,
    ): Response<Video>

    // ── Live Broadcasts ───────────────────────────────────────────────────────

    @GET("youtube/v3/liveBroadcasts")
    suspend fun liveBroadcasts(
        @Query("part") part: String = "snippet,status,contentDetails",
        @Query("broadcastStatus") broadcastStatus: String? = null,
        @Query("id") id: String? = null,
    ): LiveBroadcastListResponse

    @POST("youtube/v3/liveBroadcasts")
    suspend fun insertLiveBroadcast(
        @Query("part") part: String = "snippet,status",
        @Body body: LiveBroadcastInsertRequest,
    ): LiveBroadcast

    @POST("youtube/v3/liveBroadcasts/transition")
    suspend fun transitionLiveBroadcast(
        @Query("broadcastStatus") broadcastStatus: String,
        @Query("id") id: String,
        @Query("part") part: String = "snippet,status",
    ): LiveBroadcast

    @POST("youtube/v3/liveBroadcasts/bind")
    suspend fun bindLiveBroadcast(
        @Query("id") id: String,
        @Query("streamId") streamId: String,
        @Query("part") part: String = "snippet,contentDetails",
    ): LiveBroadcast

    // ── Live Streams ──────────────────────────────────────────────────────────

    @GET("youtube/v3/liveStreams")
    suspend fun liveStreams(
        @Query("part") part: String = "snippet,cdn,status",
        @Query("mine") mine: Boolean = true,
    ): LiveStreamListResponse

    @POST("youtube/v3/liveStreams")
    suspend fun insertLiveStream(
        @Query("part") part: String = "snippet,cdn",
        @Body body: LiveStreamInsertRequest,
    ): LiveStream

    // ── Live Chat ─────────────────────────────────────────────────────────────

    @GET("youtube/v3/liveChat/messages")
    suspend fun liveChatMessages(
        @Query("liveChatId") liveChatId: String,
        @Query("part") part: String = "snippet,authorDetails",
        @Query("pageToken") pageToken: String? = null,
    ): LiveChatMessageListResponse

    @POST("youtube/v3/liveChat/messages")
    suspend fun insertLiveChatMessage(
        @Query("part") part: String = "snippet",
        @Body body: LiveChatMessageInsertRequest,
    ): LiveChatMessage
}
