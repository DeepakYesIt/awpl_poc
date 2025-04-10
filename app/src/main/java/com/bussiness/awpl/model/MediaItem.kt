package com.bussiness.awpl.model

data class MediaItem(
    val type: MediaType,
    val uri: String
)

enum class MediaType {
    IMAGE, VIDEO, PDF
}
