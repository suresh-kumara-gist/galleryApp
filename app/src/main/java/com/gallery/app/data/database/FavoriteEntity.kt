// data/database/FavoriteEntity.kt
package com.gallery.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val mediaId: Long,
    val dateAdded: Long
)

