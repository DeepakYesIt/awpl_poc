package com.bussiness.awpl.model

import android.net.Uri

data class MediaItem(
    val type: MediaType,
    val uri: Uri
)

enum class MediaType {
    IMAGE, VIDEO, PDF
}
