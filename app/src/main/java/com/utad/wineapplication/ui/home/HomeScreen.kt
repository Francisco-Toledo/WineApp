package com.utad.wineapplication.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import java.lang.reflect.Modifier

@Composable
fun WineAppHome(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.wine_background),
            contentDescription = "Fondo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .padding(start = 16.dp, top = 100.dp)
                .align(Alignment.TopStart)
        ) {
            // Botón de tu compañero
            Button(onClick = { navController.navigate("wine_list") }) {
                Text("Explorar vinos")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tu botón de escaneo
            Button(onClick = { navController.navigate("scan") }) {
                Text("Escanear etiqueta")
            }
        }
    }
}