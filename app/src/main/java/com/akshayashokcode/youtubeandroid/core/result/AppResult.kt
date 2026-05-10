package com.akshayashokcode.youtubeandroid.core.result

import kotlinx.coroutines.CancellationException

sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Error(val throwable: Throwable, val message: String = throwable.message.orEmpty()) : AppResult<Nothing>
}

inline fun <T> runAppCatching(block: () -> T): AppResult<T> = try {
    AppResult.Success(block())
} catch (throwable: CancellationException) {
    throw throwable
} catch (throwable: Throwable) {
    AppResult.Error(throwable)
}
