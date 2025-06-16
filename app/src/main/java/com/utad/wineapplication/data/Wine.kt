package com.utad.wineapplication.data

data class Wine(
    val id: Int,
    val name: String,
    val description: String,
    val imageResource: Int,  // Para imágenes locales
    val region: String
)