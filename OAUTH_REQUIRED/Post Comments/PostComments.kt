package examples.oauth_required.postcomments

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface PostCommentsApi {
    @POST("youtube/v3/commentThreads")
    suspend fun post(@Header("Authorization") auth: String, @Query("part") part: String = "snippet", @Body body: CommentThreadRequest): CommentThreadResponse

    @DELETE("youtube/v3/comments")
    suspend fun delete(@Header("Authorization") auth: String, @Query("id") commentId: String)
}

class PostComments(private val api: PostCommentsApi, private val accessTokenProvider: suspend () -> String) {
    fun postToVideo(videoId: String, text: String): Flow<CommentThreadResponse> = flow {
        require(videoId.isNotBlank() && text.isNotBlank()) { "Video ID and comment text are required." }
        emit(api.post("Bearer ${accessTokenProvider()}", body = CommentThreadRequest(CommentThreadSnippet(videoId, TopLevelComment(CommentSnippet(text))))))
    }
}

data class CommentThreadRequest(val snippet: CommentThreadSnippet)
data class CommentThreadSnippet(val videoId: String, val topLevelComment: TopLevelComment)
data class TopLevelComment(val snippet: CommentSnippet)
data class CommentSnippet(val textOriginal: String)
data class CommentThreadResponse(val id: String)
