package com.utad.wineapplication.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utad.wineapplication.data.AppDatabase
import com.utad.wineapplication.data.ScannedText
import com.utad.wineapplication.data.ScannedTextDao
import kotlinx.coroutines.launch
import javax.inject.Inject

@ScannedTextViewModel.HiltViewModel
class OCRViewModel @Inject constructor(
    private val scannedTextDao: ScannedTextDao
) : ViewModel() {

    fun insertScannedText(imageUri: String, text: String) {
        viewModelScope.launch {
            scannedTextDao.insert(
                ScannedText(
                    imageUri = imageUri,
                    extractedText = text.uppercase() // Texto en mayúsculas
                )
            )
        }
    }
}