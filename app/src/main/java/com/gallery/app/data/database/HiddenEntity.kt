// data/database/HiddenEntity.kt
package com.gallery.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hidden")
data class HiddenEntity(
    @PrimaryKey
    val path: String,
    val dateHidden: Long
)