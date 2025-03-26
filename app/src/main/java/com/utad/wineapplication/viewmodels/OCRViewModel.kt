package com.utad.wineapplication.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.utad.wineapplication.data.ScannedText
import com.utad.wineapplication.data.ScannedTextDao
import kotlinx.coroutines.launch
import javax.inject.Inject

class OCRViewModel (
    private val scannedTextDao: ScannedTextDao
) : ViewModel() {

    private val _scannedTexts = mutableStateListOf<ScannedText>()
    val scannedTexts: List<ScannedText> get() = _scannedTexts


    fun insertScannedText(imageUri: String, text: String) {
        viewModelScope.launch {
            val newItem = ScannedText(
                imageUri = imageUri,
                extractedText = text.uppercase()
            )
            scannedTextDao.insert(newItem)
            _scannedTexts.add(newItem)
        }
    }
}
class OCRViewModelFactory(
    private val dao: ScannedTextDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OCRViewModel::class.java)) {
            return OCRViewModel(dao) as T
        }
        throw IllegalArgumentException("ViewModel class desconocida")
    }
}