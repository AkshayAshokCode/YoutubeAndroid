package com.akshayashokcode.youtubeandroid.features.oauthrequired.upload_video

import android.content.ContentResolver
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import java.io.IOException

/** The video being uploaded. Reads byte ranges on demand so the file is never copied or fully loaded. */
interface UploadSource {
    val size: Long
    val mimeType: String

    /** Reads up to [length] bytes starting at [offset]. */
    fun read(offset: Long, length: Int): ByteArray
}

/** Reads a video picked with the system file picker straight from its content:// Uri. */
class ContentUriUploadSource(
    private val resolver: ContentResolver,
    private val uri: Uri,
) : UploadSource {

    override val size: Long by lazy {
        resolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst() && !cursor.isNull(0)) cursor.getLong(0) else null
        } ?: resolver.openAssetFileDescriptor(uri, "r")?.use { it.length } ?: -1L
    }

    override val mimeType: String by lazy { resolver.getType(uri) ?: "video/*" }

    override fun read(offset: Long, length: Int): ByteArray {
        val pfd = resolver.openFileDescriptor(uri, "r") ?: throw IOException("Can't open the video file.")
        ParcelFileDescriptor.AutoCloseInputStream(pfd).use { input ->
            input.channel.position(offset)
            val buffer = ByteArray(length)
            var read = 0
            while (read < length) {
                val n = input.read(buffer, read, length - read)
                if (n < 0) break
                read += n
            }
            return if (read == length) buffer else buffer.copyOf(read)
        }
    }
}
