package com.utad.wineapplication.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScannedTextDao {
    @Insert
    suspend fun insert(scannedText: ScannedText)

    @Query("SELECT * FROM scanned_texts ORDER BY id DESC")
    fun getAll(): Flow<List<ScannedText>>

    @Delete
    suspend fun delete(scannedText: ScannedText)

    @Update
    suspend fun update(scannedText: ScannedText)
}