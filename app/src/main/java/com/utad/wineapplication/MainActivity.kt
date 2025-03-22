package com.utad.wineapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable

import com.utad.wineapplication.ui.theme.WineApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WineApplicationTheme {
                AppNavigator() // Sistema de navegación
            }
        }
    }
}

// 🔹 Configurar la navegación entre pantallas
@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { WineAppHome(navController) }
        composable("wine_list") { WineListScreen(navController) }
    }
}

// 🔹 Pantalla principal con el botón
@Composable
fun WineAppHome(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.wine_background),
            contentDescription = "Fondo de vinos",
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )

        // Contenido encima del fondo
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Bienvenido a WineApp", fontSize = 24.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navController.navigate("wine_list") }) {
                Text(text = "Explorar vinos")
            }
        }
    }
}

// 🔹 Segunda pantalla con la lista de vinos
@Composable
fun WineListScreen(navController: NavController) {
    val wineList = listOf("Cabernet Sauvignon", "Merlot", "Tempranillo", "Malbec", "Chardonnay")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Lista de Vinos", fontSize = 24.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(wineList) { wine ->
                Text(
                    text = wine,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WineAppPreview() {
    WineApplicationTheme {
        AppNavigator()
    }
}
