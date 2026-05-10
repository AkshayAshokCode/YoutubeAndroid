package examples.api_key_only.getcategoriesandregions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.GET
import retrofit2.http.Query

interface GetCategoriesAndRegionsApi {
    @GET("youtube/v3/videoCategories")
    suspend fun categories(@Query("part") part: String = "snippet", @Query("regionCode") regionCode: String, @Query("key") apiKey: String): CategoriesResponse

    @GET("youtube/v3/i18nRegions")
    suspend fun regions(@Query("part") part: String = "snippet", @Query("hl") language: String? = null, @Query("key") apiKey: String): RegionsResponse
}

class GetCategoriesAndRegions(private val api: GetCategoriesAndRegionsApi, private val apiKey: String) {
    fun categories(regionCode: String): Flow<List<VideoCategory>> = flow {
        emit(api.categories(regionCode = regionCode.uppercase(), apiKey = apiKey).items)
    }

    fun regions(language: String? = null): Flow<List<YouTubeRegion>> = flow {
        emit(api.regions(language = language, apiKey = apiKey).items)
    }
}

data class CategoriesResponse(val items: List<VideoCategory> = emptyList())
data class RegionsResponse(val items: List<YouTubeRegion> = emptyList())
data class VideoCategory(val id: String? = null, val snippet: CategorySnippet? = null)
data class CategorySnippet(val title: String? = null, val assignable: Boolean? = null)
data class YouTubeRegion(val id: String? = null, val snippet: RegionSnippet? = null)
data class RegionSnippet(val gl: String? = null, val name: String? = null)
