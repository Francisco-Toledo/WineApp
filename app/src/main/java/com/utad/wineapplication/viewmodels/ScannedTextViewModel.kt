package com.utad.wineapplication.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.utad.wineapplication.data.AppDatabase
import com.utad.wineapplication.data.ScannedText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ScannedTextViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).scannedTextDao()

    val scannedTexts: Flow<List<ScannedText>> = dao.getAll()

    fun insertScannedText(text: String, imageUri: String) {
        viewModelScope.launch {
            dao.insert(ScannedText(extractedText = text, imageUri = imageUri))
        }
    }
}