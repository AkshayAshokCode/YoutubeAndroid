package com.akshayashokcode.youtubeandroid.features.oauthrequired.captions

import com.akshayashokcode.youtubeandroid.core.model.CaptionListResponse
import com.akshayashokcode.youtubeandroid.core.network.YouTubeOAuthApiService
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import com.akshayashokcode.youtubeandroid.core.result.runAppCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class CaptionsRepository @Inject constructor(
    private val service: YouTubeOAuthApiService,
) {
    fun listCaptions(videoId: String): Flow<AppResult<CaptionListResponse>> = flow {
        emit(
            runAppCatching {
                require(videoId.isNotBlank()) { "Video ID is required." }
                service.listCaptions(videoId.trim())
            }
        )
    }

    fun uploadCaption(
        videoId: String,
        language: String,
        name: String,
        file: File,
    ): Flow<AppResult<Unit>> = flow {
        emit(
            runAppCatching {
                require(videoId.isNotBlank()) { "Video ID is required." }
                require(language.isNotBlank()) { "Language code is required." }
                val part = MultipartBody.Part.createFormData(
                    "body",
                    file.name,
                    file.asRequestBody("text/vtt".toMediaTypeOrNull()),
                )
                service.uploadCaption(
                    videoId = videoId.trim(),
                    language = language.trim(),
                    name = name.trim(),
                    file = part,
                )
            }
        )
    }

    fun deleteCaption(captionId: String): Flow<AppResult<Unit>> = flow {
        emit(
            runAppCatching {
                require(captionId.isNotBlank()) { "Caption ID is required." }
                service.deleteCaption(captionId.trim())
            }
        )
    }
}
