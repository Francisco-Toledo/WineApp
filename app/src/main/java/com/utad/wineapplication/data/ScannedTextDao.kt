package com.utad.wineapplication.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScannedTextDao {
    @Insert
    suspend fun insert(scannedText: ScannedText)

    @Query("SELECT * FROM scanned_texts ORDER BY id DESC")
    fun getAll(): Flow<List<ScannedText>>
}