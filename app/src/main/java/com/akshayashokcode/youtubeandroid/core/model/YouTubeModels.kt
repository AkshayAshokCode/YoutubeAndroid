package com.akshayashokcode.youtubeandroid.core.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PageInfo(
    val totalResults: Int? = null,
    val resultsPerPage: Int? = null,
)

@JsonClass(generateAdapter = true)
data class Thumbnail(
    val url: String? = null,
    val width: Int? = null,
    val height: Int? = null,
)

@JsonClass(generateAdapter = true)
data class Thumbnails(
    @Json(name = "default") val defaultThumbnail: Thumbnail? = null,
    val medium: Thumbnail? = null,
    val high: Thumbnail? = null,
    val standard: Thumbnail? = null,
    val maxres: Thumbnail? = null,
)

@JsonClass(generateAdapter = true)
data class LocalizedText(
    val title: String? = null,
    val description: String? = null,
)

@JsonClass(generateAdapter = true)
data class SearchListResponse(
    val nextPageToken: String? = null,
    val prevPageToken: String? = null,
    val regionCode: String? = null,
    val pageInfo: PageInfo? = null,
    val items: List<SearchResult> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class SearchResult(
    val id: SearchId? = null,
    val snippet: Snippet? = null,
)

@JsonClass(generateAdapter = true)
data class SearchId(
    val kind: String? = null,
    val videoId: String? = null,
    val channelId: String? = null,
    val playlistId: String? = null,
)

@JsonClass(generateAdapter = true)
data class VideoListResponse(
    val nextPageToken: String? = null,
    val prevPageToken: String? = null,
    val pageInfo: PageInfo? = null,
    val items: List<Video> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class Video(
    val id: String? = null,
    val snippet: Snippet? = null,
    val contentDetails: VideoContentDetails? = null,
    val statistics: VideoStatistics? = null,
    val status: VideoStatus? = null,
    val liveStreamingDetails: LiveStreamingDetails? = null,
)

@JsonClass(generateAdapter = true)
data class VideoContentDetails(
    val duration: String? = null,
    val dimension: String? = null,
    val definition: String? = null,
    val caption: String? = null,
    val licensedContent: Boolean? = null,
)

@JsonClass(generateAdapter = true)
data class VideoStatistics(
    val viewCount: String? = null,
    val likeCount: String? = null,
    val favoriteCount: String? = null,
    val commentCount: String? = null,
)

@JsonClass(generateAdapter = true)
data class VideoStatus(
    val uploadStatus: String? = null,
    val privacyStatus: String? = null,
    val license: String? = null,
    val embeddable: Boolean? = null,
    val publicStatsViewable: Boolean? = null,
    val madeForKids: Boolean? = null,
)

@JsonClass(generateAdapter = true)
data class LiveStreamingDetails(
    val actualStartTime: String? = null,
    val actualEndTime: String? = null,
    val scheduledStartTime: String? = null,
    val scheduledEndTime: String? = null,
    val concurrentViewers: String? = null,
    val activeLiveChatId: String? = null,
)

@JsonClass(generateAdapter = true)
data class ChannelListResponse(
    val nextPageToken: String? = null,
    val prevPageToken: String? = null,
    val pageInfo: PageInfo? = null,
    val items: List<Channel> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class Channel(
    val id: String? = null,
    val snippet: Snippet? = null,
    val contentDetails: ChannelContentDetails? = null,
    val statistics: ChannelStatistics? = null,
    val brandingSettings: BrandingSettings? = null,
)

@JsonClass(generateAdapter = true)
data class ChannelContentDetails(
    val relatedPlaylists: RelatedPlaylists? = null,
)

@JsonClass(generateAdapter = true)
data class RelatedPlaylists(
    val likes: String? = null,
    val uploads: String? = null,
)

@JsonClass(generateAdapter = true)
data class ChannelStatistics(
    val viewCount: String? = null,
    val subscriberCount: String? = null,
    val hiddenSubscriberCount: Boolean? = null,
    val videoCount: String? = null,
)

@JsonClass(generateAdapter = true)
data class BrandingSettings(
    val channel: BrandingChannel? = null,
)

@JsonClass(generateAdapter = true)
data class BrandingChannel(
    val title: String? = null,
    val description: String? = null,
    val keywords: String? = null,
    val country: String? = null,
)

@JsonClass(generateAdapter = true)
data class PlaylistListResponse(
    val nextPageToken: String? = null,
    val prevPageToken: String? = null,
    val pageInfo: PageInfo? = null,
    val items: List<Playlist> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class Playlist(
    val id: String? = null,
    val snippet: Snippet? = null,
    val contentDetails: PlaylistContentDetails? = null,
    val status: PlaylistStatus? = null,
)

@JsonClass(generateAdapter = true)
data class PlaylistContentDetails(
    val itemCount: Int? = null,
)

@JsonClass(generateAdapter = true)
data class PlaylistStatus(
    val privacyStatus: String? = null,
)

@JsonClass(generateAdapter = true)
data class PlaylistItemListResponse(
    val nextPageToken: String? = null,
    val prevPageToken: String? = null,
    val pageInfo: PageInfo? = null,
    val items: List<PlaylistItem> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class PlaylistItem(
    val id: String? = null,
    val snippet: PlaylistItemSnippet? = null,
    val contentDetails: PlaylistItemContentDetails? = null,
    val status: PlaylistStatus? = null,
)

@JsonClass(generateAdapter = true)
data class PlaylistItemSnippet(
    val publishedAt: String? = null,
    val channelId: String? = null,
    val title: String? = null,
    val description: String? = null,
    val thumbnails: Thumbnails? = null,
    val channelTitle: String? = null,
    val playlistId: String? = null,
    val position: Int? = null,
    val resourceId: ResourceId? = null,
)

@JsonClass(generateAdapter = true)
data class ResourceId(
    val kind: String? = null,
    val videoId: String? = null,
    val channelId: String? = null,
)

@JsonClass(generateAdapter = true)
data class PlaylistItemContentDetails(
    val videoId: String? = null,
    val videoPublishedAt: String? = null,
)

@JsonClass(generateAdapter = true)
data class VideoCategoryListResponse(
    val items: List<VideoCategory> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class VideoCategory(
    val id: String? = null,
    val snippet: VideoCategorySnippet? = null,
)

@JsonClass(generateAdapter = true)
data class VideoCategorySnippet(
    val title: String? = null,
    val assignable: Boolean? = null,
    val channelId: String? = null,
)

@JsonClass(generateAdapter = true)
data class RegionListResponse(
    val items: List<Region> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class Region(
    val id: String? = null,
    val snippet: RegionSnippet? = null,
)

@JsonClass(generateAdapter = true)
data class RegionSnippet(
    val gl: String? = null,
    val name: String? = null,
)

@JsonClass(generateAdapter = true)
data class Snippet(
    val publishedAt: String? = null,
    val channelId: String? = null,
    val title: String? = null,
    val description: String? = null,
    val thumbnails: Thumbnails? = null,
    val channelTitle: String? = null,
    val tags: List<String>? = null,
    val categoryId: String? = null,
    val liveBroadcastContent: String? = null,
    val localized: LocalizedText? = null,
    val defaultLanguage: String? = null,
    val defaultAudioLanguage: String? = null,
)

// ── Comments ─────────────────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class CommentThreadListResponse(
    val nextPageToken: String? = null,
    val pageInfo: PageInfo? = null,
    val items: List<CommentThread> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class CommentThread(
    val id: String? = null,
    val snippet: CommentThreadSnippet? = null,
)

@JsonClass(generateAdapter = true)
data class CommentThreadSnippet(
    val videoId: String? = null,
    val topLevelComment: Comment? = null,
    val totalReplyCount: Int? = null,
    val isPublic: Boolean? = null,
)

@JsonClass(generateAdapter = true)
data class Comment(
    val id: String? = null,
    val snippet: CommentSnippet? = null,
)

@JsonClass(generateAdapter = true)
data class CommentSnippet(
    val textDisplay: String? = null,
    val textOriginal: String? = null,
    val authorDisplayName: String? = null,
    val authorProfileImageUrl: String? = null,
    val likeCount: Long? = null,
    val publishedAt: String? = null,
    val updatedAt: String? = null,
    val videoId: String? = null,
)

@JsonClass(generateAdapter = true)
data class CommentThreadInsertRequest(
    val snippet: CommentThreadInsertSnippet,
)

@JsonClass(generateAdapter = true)
data class CommentThreadInsertSnippet(
    val videoId: String,
    val topLevelComment: CommentInsertBody,
)

@JsonClass(generateAdapter = true)
data class CommentInsertBody(
    val snippet: CommentInsertSnippet,
)

@JsonClass(generateAdapter = true)
data class CommentInsertSnippet(
    val textOriginal: String,
)

// ── Subscriptions ─────────────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class SubscriptionListResponse(
    val nextPageToken: String? = null,
    val pageInfo: PageInfo? = null,
    val items: List<Subscription> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class Subscription(
    val id: String? = null,
    val snippet: SubscriptionSnippet? = null,
)

@JsonClass(generateAdapter = true)
data class SubscriptionSnippet(
    val channelId: String? = null,
    val title: String? = null,
    val description: String? = null,
    val publishedAt: String? = null,
    val thumbnails: Thumbnails? = null,
    val resourceId: ResourceId? = null,
)

@JsonClass(generateAdapter = true)
data class SubscriptionInsertRequest(
    val snippet: SubscriptionInsertSnippet,
)

@JsonClass(generateAdapter = true)
data class SubscriptionInsertSnippet(
    val resourceId: ResourceId,
)

// ── Captions ──────────────────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class CaptionListResponse(
    val items: List<Caption> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class Caption(
    val id: String? = null,
    val snippet: CaptionSnippet? = null,
)

@JsonClass(generateAdapter = true)
data class CaptionSnippet(
    val videoId: String? = null,
    val lastUpdated: String? = null,
    val trackKind: String? = null,
    val language: String? = null,
    val name: String? = null,
    val audioTrackType: String? = null,
    val isCC: Boolean? = null,
    val isDraft: Boolean? = null,
    val status: String? = null,
)

// ── Live Broadcasting ─────────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class LiveBroadcastListResponse(
    val nextPageToken: String? = null,
    val pageInfo: PageInfo? = null,
    val items: List<LiveBroadcast> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class LiveBroadcast(
    val id: String? = null,
    val snippet: LiveBroadcastSnippet? = null,
    val status: LiveBroadcastStatus? = null,
    val contentDetails: LiveBroadcastContentDetails? = null,
)

@JsonClass(generateAdapter = true)
data class LiveBroadcastSnippet(
    val title: String? = null,
    val description: String? = null,
    val scheduledStartTime: String? = null,
    val actualStartTime: String? = null,
    val actualEndTime: String? = null,
    val liveChatId: String? = null,
    val thumbnails: Thumbnails? = null,
)

@JsonClass(generateAdapter = true)
data class LiveBroadcastStatus(
    val lifeCycleStatus: String? = null,
    val privacyStatus: String? = null,
    val recordingStatus: String? = null,
)

@JsonClass(generateAdapter = true)
data class LiveBroadcastContentDetails(
    val boundStreamId: String? = null,
    val enableAutoStart: Boolean? = null,
    val enableAutoStop: Boolean? = null,
)

@JsonClass(generateAdapter = true)
data class LiveBroadcastInsertRequest(
    val snippet: LiveBroadcastInsertSnippet,
    val status: LiveBroadcastInsertStatus,
)

@JsonClass(generateAdapter = true)
data class LiveBroadcastInsertSnippet(
    val title: String,
    val scheduledStartTime: String,
    val description: String? = null,
)

@JsonClass(generateAdapter = true)
data class LiveBroadcastInsertStatus(
    val privacyStatus: String,
)

// ── Live Streams ──────────────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class LiveStreamListResponse(
    val items: List<LiveStream> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class LiveStream(
    val id: String? = null,
    val snippet: LiveStreamSnippet? = null,
    val cdn: LiveStreamCdn? = null,
    val status: LiveStreamStatus? = null,
)

@JsonClass(generateAdapter = true)
data class LiveStreamSnippet(
    val title: String? = null,
    val description: String? = null,
)

@JsonClass(generateAdapter = true)
data class LiveStreamCdn(
    val ingestionType: String? = null,
    val ingestionInfo: LiveStreamIngestionInfo? = null,
    val resolution: String? = null,
    val frameRate: String? = null,
)

@JsonClass(generateAdapter = true)
data class LiveStreamIngestionInfo(
    val streamName: String? = null,
    val ingestionAddress: String? = null,
    val backupIngestionAddress: String? = null,
)

@JsonClass(generateAdapter = true)
data class LiveStreamStatus(
    val streamStatus: String? = null,
)

@JsonClass(generateAdapter = true)
data class LiveStreamInsertRequest(
    val snippet: LiveStreamInsertSnippet,
    val cdn: LiveStreamCdn,
)

@JsonClass(generateAdapter = true)
data class LiveStreamInsertSnippet(
    val title: String,
    val description: String? = null,
)

// ── Live Chat ─────────────────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class LiveChatMessageListResponse(
    val nextPageToken: String? = null,
    val pollingIntervalMillis: Long? = null,
    val pageInfo: PageInfo? = null,
    val items: List<LiveChatMessage> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class LiveChatMessage(
    val id: String? = null,
    val snippet: LiveChatMessageSnippet? = null,
    val authorDetails: LiveChatAuthorDetails? = null,
)

@JsonClass(generateAdapter = true)
data class LiveChatMessageSnippet(
    val type: String? = null,
    val liveChatId: String? = null,
    val authorChannelId: String? = null,
    val publishedAt: String? = null,
    val displayMessage: String? = null,
    val textMessageDetails: TextMessageDetails? = null,
)

@JsonClass(generateAdapter = true)
data class TextMessageDetails(
    val messageText: String? = null,
)

@JsonClass(generateAdapter = true)
data class LiveChatAuthorDetails(
    val channelId: String? = null,
    val displayName: String? = null,
    val profileImageUrl: String? = null,
    val isChatOwner: Boolean? = null,
    val isChatModerator: Boolean? = null,
)

@JsonClass(generateAdapter = true)
data class LiveChatMessageInsertRequest(
    val snippet: LiveChatMessageInsertSnippet,
)

@JsonClass(generateAdapter = true)
data class LiveChatMessageInsertSnippet(
    val liveChatId: String,
    val type: String = "textMessageEvent",
    val textMessageDetails: TextMessageDetails,
)

// ── Playlist write bodies ─────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class PlaylistInsertRequest(
    val snippet: PlaylistInsertSnippet,
    val status: PlaylistStatus,
)

@JsonClass(generateAdapter = true)
data class PlaylistInsertSnippet(
    val title: String,
    val description: String? = null,
)

@JsonClass(generateAdapter = true)
data class PlaylistItemInsertRequest(
    val snippet: PlaylistItemInsertSnippet,
)

@JsonClass(generateAdapter = true)
data class PlaylistItemInsertSnippet(
    val playlistId: String,
    val resourceId: ResourceId,
)
