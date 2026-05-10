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
