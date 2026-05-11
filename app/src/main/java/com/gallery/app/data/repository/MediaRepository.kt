// data/repository/MediaRepository.kt
package com.gallery.app.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.gallery.app.data.model.MediaItem
import com.gallery.app.data.model.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File

class MediaRepository(private val contentResolver: ContentResolver) {
    
    fun getAllMedia(): Flow<List<MediaItem>> = flow {
        val allMedia = mutableListOf<MediaItem>()
        
        allMedia.addAll(getImages())
        allMedia.addAll(getVideos())
        allMedia.addAll(getDocuments())
        
        emit(allMedia.sortedByDescending { it.dateModified })
    }
    
    suspend fun getImages(): List<MediaItem> = withContext(Dispatchers.IO) {
        val images = mutableListOf<MediaItem>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT
        )
        
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        )
        
        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val dateColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
            val mimeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
            val widthColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
            val heightColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
            
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val path = it.getString(dataColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                
                val mediaItem = MediaItem(
                    id = id,
                    uri = contentUri,
                    name = it.getString(nameColumn),
                    path = path,
                    size = it.getLong(sizeColumn),
                    dateModified = it.getLong(dateColumn),
                    type = MediaType.IMAGE,
                    mimeType = it.getString(mimeColumn),
                    parentFolder = File(path).parentFile?.name ?: "Unknown",
                    width = it.getInt(widthColumn),
                    height = it.getInt(heightColumn)
                )
                images.add(mediaItem)
            }
        }
        
        return@withContext images
    }
    
    suspend fun getVideos(): List<MediaItem> = withContext(Dispatchers.IO) {
        val videos = mutableListOf<MediaItem>()
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT
        )
        
        val cursor = contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Video.Media.DATE_MODIFIED} DESC"
        )
        
        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val dateColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)
            val mimeColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)
            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val widthColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)
            val heightColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)
            
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val path = it.getString(dataColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                
                val mediaItem = MediaItem(
                    id = id,
                    uri = contentUri,
                    name = it.getString(nameColumn),
                    path = path,
                    size = it.getLong(sizeColumn),
                    dateModified = it.getLong(dateColumn),
                    type = MediaType.VIDEO,
                    mimeType = it.getString(mimeColumn),
                    parentFolder = File(path).parentFile?.name ?: "Unknown",
                    duration = it.getLong(durationColumn),
                    width = it.getInt(widthColumn),
                    height = it.getInt(heightColumn)
                )
                videos.add(mediaItem)
            }
        }
        
        return@withContext videos
    }
    
    suspend fun getDocuments(): List<MediaItem> = withContext(Dispatchers.IO) {
        val documents = mutableListOf<MediaItem>()
        val extensions = listOf("pdf", "doc", "docx", "txt", "ppt", "pptx", "xls", "xlsx")
        
        extensions.forEach { extension ->
            val directory = File("/storage/emulated/0/Download")
            if (directory.exists()) {
                directory.listFiles()?.forEach { file ->
                    if (file.extension.equals(extension, ignoreCase = true)) {
                        val uri = androidx.core.content.FileProvider.getUriForFile(
                            contentResolver.context,
                            "${contentResolver.context.packageName}.fileprovider",
                            file
                        )
                        
                        val mediaType = when (extension.lowercase()) {
                            "pdf" -> MediaType.PDF
                            "txt" -> MediaType.TEXT
                            "doc", "docx" -> MediaType.DOCUMENT
                            "ppt", "pptx" -> MediaType.PRESENTATION
                            "xls", "xlsx" -> MediaType.SPREADSHEET
                            else -> MediaType.OTHER
                        }
                        
                        val mediaItem = MediaItem(
                            id = file.hashCode().toLong(),
                            uri = uri,
                            name = file.name,
                            path = file.absolutePath,
                            size = file.length(),
                            dateModified = file.lastModified(),
                            type = mediaType,
                            mimeType = getMimeType(file.extension),
                            parentFolder = file.parentFile?.name ?: "Unknown"
                        )
                        documents.add(mediaItem)
                    }
                }
            }
        }
        
        return@withContext documents
    }
    
    private fun getMimeType(extension: String): String {
        return when (extension.lowercase()) {
            "pdf" -> "application/pdf"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "txt" -> "text/plain"
            "ppt" -> "application/vnd.ms-powerpoint"
            "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            "xls" -> "application/vnd.ms-excel"
            "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            else -> "application/octet-stream"
        }
    }
}