package com.utad.wineapplication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.utad.wineapplication.data.ScannedText
import com.utad.wineapplication.data.ScannedTextDao
import com.utad.wineapplication.repository.WineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class WineListViewModel(
    private val scannedTextDao: ScannedTextDao
) : ViewModel() {

    val scannedTexts: Flow<List<ScannedText>> = scannedTextDao.getAll()

    // Combinamos vinos predefinidos con escaneos del usuario
    val allItems: Flow<List<Any>> = scannedTextDao.getAll().map { scannedList ->
        val combined = mutableListOf<Any>()
        combined.addAll(WineRepository.predefinedWines)
        combined.addAll(scannedList)
        combined
    }

    fun deleteItem(item: ScannedText) {
        viewModelScope.launch {
            scannedTextDao.delete(item)
        }
    }

    fun updateItem(item: ScannedText) {
        viewModelScope.launch {
            scannedTextDao.update(item)
        }
    }
}
class WineListViewModelFactory(
    private val scannedTextDao: ScannedTextDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WineListViewModel::class.java)) {
            return WineListViewModel(scannedTextDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
