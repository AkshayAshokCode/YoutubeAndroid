package com.akshayashokcode.youtubeandroid.features.oauthrequired.comments

import com.akshayashokcode.youtubeandroid.core.model.CommentInsertBody
import com.akshayashokcode.youtubeandroid.core.model.CommentInsertSnippet
import com.akshayashokcode.youtubeandroid.core.model.CommentThread
import com.akshayashokcode.youtubeandroid.core.model.CommentThreadInsertRequest
import com.akshayashokcode.youtubeandroid.core.model.CommentThreadInsertSnippet
import com.akshayashokcode.youtubeandroid.core.network.YouTubeOAuthApiService
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import com.akshayashokcode.youtubeandroid.core.result.runAppCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CommentsRepository @Inject constructor(
    private val service: YouTubeOAuthApiService,
) {
    fun postComment(videoId: String, text: String): Flow<AppResult<CommentThread>> = flow {
        emit(
            runAppCatching {
                require(videoId.isNotBlank()) { "Video ID is required." }
                require(text.isNotBlank()) { "Comment text is required." }
                service.insertCommentThread(
                    body = CommentThreadInsertRequest(
                        snippet = CommentThreadInsertSnippet(
                            videoId = videoId.trim(),
                            topLevelComment = CommentInsertBody(
                                snippet = CommentInsertSnippet(textOriginal = text.trim()),
                            ),
                        ),
                    ),
                )
            }
        )
    }

    fun deleteComment(commentId: String): Flow<AppResult<Unit>> = flow {
        emit(
            runAppCatching {
                require(commentId.isNotBlank()) { "Comment ID is required." }
                service.deleteComment(commentId.trim())
            }
        )
    }
}
