package com.akshayashokcode.youtubeandroid.features.apikeyonly.categories_regions

import com.akshayashokcode.youtubeandroid.core.model.RegionListResponse
import com.akshayashokcode.youtubeandroid.core.model.VideoCategoryListResponse
import com.akshayashokcode.youtubeandroid.core.network.YouTubeApiService
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import com.akshayashokcode.youtubeandroid.core.result.runAppCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CategoriesRegionsRepository @Inject constructor(
    private val service: YouTubeApiService,
) {
    fun getVideoCategories(regionCode: String = "US"): Flow<AppResult<VideoCategoryListResponse>> = flow {
        emit(runAppCatching { service.videoCategories(regionCode = regionCode) })
    }

    fun getSupportedRegions(language: String? = null): Flow<AppResult<RegionListResponse>> = flow {
        emit(runAppCatching { service.regions(language = language) })
    }
}
