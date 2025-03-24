package com.utad.wineapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scanned_texts")
data class ScannedText(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imageUri: String,
    val extractedText: String
)