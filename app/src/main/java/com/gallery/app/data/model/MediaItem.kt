// data/model/MediaItem.kt
package com.gallery.app.data.model

import android.net.Uri

data class MediaItem(
    val id: Long,
    val uri: Uri,
    val name: String,
    val path: String,
    val size: Long,
    val dateModified: Long,
    val type: MediaType,
    val mimeType: String,
    val parentFolder: String,
    val duration: Long = 0,
    val width: Int = 0,
    val height: Int = 0
)

enum class MediaType {
    IMAGE,
    VIDEO,
    PDF,
    DOCUMENT,
    TEXT,
    SPREADSHEET,
    PRESENTATION,
    OTHER
}
