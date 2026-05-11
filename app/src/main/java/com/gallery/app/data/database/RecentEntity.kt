// data/database/RecentEntity.kt
package com.gallery.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent")
data class RecentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mediaId: Long,
    val path: String,
    val viewedAt: Long
)
