// data/database/HiddenDao.kt
package com.gallery.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query

@Dao
interface HiddenDao {
    @Insert
    suspend fun insert(hidden: HiddenEntity)
    
    @Delete
    suspend fun delete(hidden: HiddenEntity)
    
    @Query("SELECT * FROM hiddens")
    suspend fun getAllHiddens(): List<HiddenEntity>
    
    @Query("SELECT EXISTS(SELECT 1 FROM hiddens WHERE mediaId = :mediaId)")
    suspend fun isHidden(mediaId: Long): Boolean
}