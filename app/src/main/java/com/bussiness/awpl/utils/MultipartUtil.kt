package com.bussiness.awpl.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MultipartUtil {

    companion object {
        fun uriToMultipart(
            context: Context,
            uri: Uri,
            partName: String = "file"
        ): MultipartBody.Part? {
            val contentResolver = context.contentResolver

            val fileName = getFileName(contentResolver, uri) ?: return null
            val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"

            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val fileBytes = inputStream.use { it.readBytes() }

            val requestFile = RequestBody.create(mimeType.toMediaTypeOrNull(), fileBytes)

            return MultipartBody.Part.createFormData(partName, fileName, requestFile)
        }

        private fun getFileName(contentResolver: ContentResolver, uri: Uri): String? {
            var name: String? = null
            val returnCursor = contentResolver.query(uri, null, null, null, null)
            returnCursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        name = it.getString(nameIndex)
                    }
                }
            }
            return name
        }

        fun stringToRequestBody(value: String, mediaType: String = "text/plain"): RequestBody {
            return RequestBody.create(mediaType.toMediaTypeOrNull(), value)
        }

        fun ensureStartsWithSlash(path: String): String {
            return if (path.startsWith("/")) path else "/$path"
        }
    }
}