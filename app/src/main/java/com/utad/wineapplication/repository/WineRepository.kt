package com.utad.wineapplication.repository

import com.utad.wineapplication.R
import com.utad.wineapplication.data.Wine

object WineRepository {
    val predefinedWines = listOf(
        Wine(
            id = 1,
            name = "Cune",
            description = "Vino tinto con cuerpo",
            imageResource = R.drawable.img,
            region = "La Rioja"
        ),
        Wine(
            id = 2,
            name = "Señorío de Ondas",
            description = "Vino blanco",
            imageResource = R.drawable.img1,
            region = "Rueda"
        ),
        Wine(
            id = 3,
            name = "Peñascal",
            description = "Vino rosado y afrutado",
            imageResource = R.drawable.img2,
            region = "Rosado"
        ),
        Wine(
            id = 4,
            name = "Freixenet",
            description = "Burbujeante",
            imageResource = R.drawable.img3,
            region = "Espumoso Cava"
        ),
        Wine(
            id = 5,
            name = "Moscatel",
            description = "Aromático y con toques de miel",
            imageResource = R.drawable.img4,
            region = "Dulce"
        )
    )
}