package com.akshayashokcode.youtubeandroid.core.network

import com.akshayashokcode.youtubeandroid.core.model.ChannelListResponse
import com.akshayashokcode.youtubeandroid.core.model.PlaylistItemListResponse
import com.akshayashokcode.youtubeandroid.core.model.PlaylistListResponse
import com.akshayashokcode.youtubeandroid.core.model.RegionListResponse
import com.akshayashokcode.youtubeandroid.core.model.SearchListResponse
import com.akshayashokcode.youtubeandroid.core.model.VideoCategoryListResponse
import com.akshayashokcode.youtubeandroid.core.model.VideoListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApiService {
    @GET("youtube/v3/search")
    suspend fun search(
        @Query("part") part: String = YouTubeParts.SEARCH,
        @Query("q") query: String,
        @Query("type") type: String? = null,
        @Query("channelId") channelId: String? = null,
        @Query("pageToken") pageToken: String? = null,
        @Query("maxResults") maxResults: Int = 25,
    ): SearchListResponse

    @GET("youtube/v3/videos")
    suspend fun videos(
        @Query("part") part: String = YouTubeParts.VIDEOS,
        @Query("id") ids: String? = null,
        @Query("chart") chart: String? = null,
        @Query("regionCode") regionCode: String? = null,
        @Query("videoCategoryId") videoCategoryId: String? = null,
        @Query("pageToken") pageToken: String? = null,
        @Query("maxResults") maxResults: Int = 25,
    ): VideoListResponse

    @GET("youtube/v3/channels")
    suspend fun channels(
        @Query("part") part: String = YouTubeParts.CHANNELS,
        @Query("id") ids: String? = null,
        @Query("forUsername") username: String? = null,
        @Query("mine") mine: Boolean? = null,
        @Query("pageToken") pageToken: String? = null,
        @Query("maxResults") maxResults: Int = 25,
    ): ChannelListResponse

    @GET("youtube/v3/playlists")
    suspend fun playlists(
        @Query("part") part: String = YouTubeParts.PLAYLISTS,
        @Query("id") ids: String? = null,
        @Query("channelId") channelId: String? = null,
        @Query("mine") mine: Boolean? = null,
        @Query("pageToken") pageToken: String? = null,
        @Query("maxResults") maxResults: Int = 25,
    ): PlaylistListResponse

    @GET("youtube/v3/playlistItems")
    suspend fun playlistItems(
        @Query("part") part: String = YouTubeParts.PLAYLIST_ITEMS,
        @Query("playlistId") playlistId: String,
        @Query("pageToken") pageToken: String? = null,
        @Query("maxResults") maxResults: Int = 25,
    ): PlaylistItemListResponse

    @GET("youtube/v3/videoCategories")
    suspend fun videoCategories(
        @Query("part") part: String = YouTubeParts.CATEGORIES,
        @Query("regionCode") regionCode: String? = null,
        @Query("id") ids: String? = null,
    ): VideoCategoryListResponse

    @GET("youtube/v3/i18nRegions")
    suspend fun regions(
        @Query("part") part: String = YouTubeParts.REGIONS,
        @Query("hl") language: String? = null,
    ): RegionListResponse
}
