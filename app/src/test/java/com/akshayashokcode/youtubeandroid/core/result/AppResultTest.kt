package com.akshayashokcode.youtubeandroid.core.result

import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class AppResultTest {

    private fun httpError(code: Int, body: String) =
        HttpException(Response.error<Unit>(code, body.toResponseBody()))

    @Test
    fun `API error shows Google's message and reason`() {
        val error = httpError(
            403,
            """{"error":{"code":403,"message":"The user is not enabled for live streaming.",""" +
                """"errors":[{"reason":"liveStreamingNotEnabled"}]}}""",
        )

        assertEquals(
            "The user is not enabled for live streaming. (HTTP 403, liveStreamingNotEnabled)",
            AppResult.Error(error).message,
        )
    }

    @Test
    fun `non-JSON error body falls back to the status code`() {
        assertEquals("HTTP 502", AppResult.Error(httpError(502, "<html>Bad Gateway</html>")).message)
    }

    @Test
    fun `other exceptions keep their own message`() {
        assertEquals("Title is required.", AppResult.Error(IllegalArgumentException("Title is required.")).message)
    }
}
