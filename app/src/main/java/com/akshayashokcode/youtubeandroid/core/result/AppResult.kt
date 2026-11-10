package com.akshayashokcode.youtubeandroid.core.result

import com.squareup.moshi.Moshi
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Error(val throwable: Throwable, val message: String = throwable.userMessage()) : AppResult<Nothing>
}

inline fun <T> runAppCatching(block: () -> T): AppResult<T> = try {
    AppResult.Success(block())
} catch (throwable: CancellationException) {
    throw throwable
} catch (throwable: Throwable) {
    AppResult.Error(throwable)
}

/**
 * For API errors, Google's own explanation instead of Retrofit's bare "HTTP 403".
 * Error bodies look like:
 *   {"error": {"code": 403, "message": "...", "errors": [{"reason": "liveStreamingNotEnabled", ...}]}}
 * The reason code is what to search for in the YouTube Data API error docs.
 */
fun Throwable.userMessage(): String {
    if (this !is HttpException) return message.orEmpty()
    val fallback = "HTTP ${code()}"
    val body = runCatching { response()?.errorBody()?.string() }.getOrNull() ?: return fallback
    val error = runCatching { errorAdapter.fromJson(body)?.get("error") as? Map<*, *> }.getOrNull()
        ?: return fallback
    val apiMessage = (error["message"] as? String)?.takeIf { it.isNotBlank() } ?: return fallback
    val reason = ((error["errors"] as? List<*>)?.firstOrNull() as? Map<*, *>)?.get("reason") as? String
    return buildString {
        append(apiMessage)
        append(" (HTTP ").append(code())
        reason?.let { append(", ").append(it) }
        append(")")
    }
}

private val errorAdapter by lazy { Moshi.Builder().build().adapter(Map::class.java) }
