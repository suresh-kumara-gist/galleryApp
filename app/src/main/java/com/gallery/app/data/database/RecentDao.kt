// data/database/RecentDao.kt
package com.gallery.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query

@Dao
interface RecentDao {
    @Insert
    suspend fun insert(recent: RecentEntity)
    
    @Delete
    suspend fun delete(recent: RecentEntity)
    
    @Query("SELECT * FROM Recents")
    suspend fun getAllRecents(): List<RecentEntity>
    
    @Query("SELECT EXISTS(SELECT 1 FROM recents WHERE mediaId = :mediaId)")
    suspend fun isRecent(mediaId: Long): Boolean
}