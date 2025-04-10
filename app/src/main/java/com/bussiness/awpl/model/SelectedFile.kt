package com.bussiness.awpl.model

import android.net.Uri

data class SelectedFile(
    val uri: Uri,
    val name: String,
    val type: String
)
