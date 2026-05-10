package com.akshayashokcode.youtubeandroid.core.network

object YouTubeParts {
    const val SEARCH = "snippet"
    const val VIDEOS = "snippet,contentDetails,statistics,status,liveStreamingDetails"
    const val CHANNELS = "snippet,contentDetails,statistics,brandingSettings"
    const val PLAYLISTS = "snippet,contentDetails,status"
    const val PLAYLIST_ITEMS = "snippet,contentDetails,status"
    const val CATEGORIES = "snippet"
    const val REGIONS = "snippet"
}
