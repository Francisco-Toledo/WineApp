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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.utad.wineapplication.ui.scan.ScanScreen
import com.utad.wineapplication.ui.theme.WineApplicationTheme
import androidx.compose.ui.tooling.preview.Preview as Preview1
import androidx.lifecycle.viewmodel.compose.viewModel
import com.utad.wineapplication.data.AppDatabase
import com.utad.wineapplication.viewmodels.OCRViewModel
import com.utad.wineapplication.viewmodels.OCRViewModelFactory

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
       // composable("homeNavigation") { WineAppHomeNavigation(navController) }
        composable("home"){ WineAppHome(navController) }
        composable("wine_list") { WineListScreen(navController) }
        composable("scan") { ScanScreen(navController) }
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
                .padding(start = 16.dp, top = 100.dp),  // Agrega padding para moverlo a la izquierda y hacia arriba
            verticalArrangement = Arrangement.Top,   // Alinea el contenido hacia la parte superior
            horizontalAlignment = Alignment.Start    // Alinea el contenido hacia la izquierda
        ) {
            Text(text = "Bienvenido a WineApp", fontSize = 24.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(25.dp))

            // Botón de tu compañero
            Button(onClick = { navController.navigate("wine_list") }) {
                Text("Explorar vinos")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = { navController.navigate("scan") }) {
                Text(text = "Escanear vino")
            }
        }
    }
}

// 🔹 Segunda pantalla con la lista de vinos
@Composable
fun WineListScreen(navController: NavController) {
    // Lista estática temporal (sin ViewModel)
    val wineList = listOf("Cabernet Sauvignon", "Merlot", "Tempranillo", "Malbec", "Chardonnay")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Lista de vinos
        Text("Nuestros vinos", style = MaterialTheme.typography.headlineSmall)
        LazyColumn {
            items(wineList) { wine ->
                Text(wine, modifier = Modifier.padding(8.dp))
            }
        }

        Button(onClick = { navController.popBackStack() }) {
            Text("Volver")
        }
    }
}
/*fun WineListScreen(
    navController: NavController,
    viewModel: OCRViewModel = viewModel(
        factory = OCRViewModelFactory(
            AppDatabase.getDatabase(LocalContext.current).scannedTextDao()
        )
    )
) {
    val scannedTexts = viewModel.scannedTexts

    // Lista estática de vinos (puedes moverla a un repositorio)
    val wineList = listOf("Cabernet Sauvignon", "Merlot", "Tempranillo", "Malbec", "Chardonnay")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Lista de vinos estaticos
        Text("Nuestros vinos", style = MaterialTheme.typography.headlineSmall)
        LazyColumn {
            items(wineList) { wine ->
                Text(wine, modifier = Modifier.padding(8.dp))
            }
        }

        // Lista de textos escaneados
        Text("Textos escaneados:", style = MaterialTheme.typography.headlineSmall)
        LazyColumn {
            items(viewModel.scannedTexts) { scanned ->
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("Imagen: ${scanned.imageUri}")
                    Text("Texto: ${scanned.extractedText}")
                }
            }
        }

        // Botón para volver
        Button(onClick = { navController.navigateUp() }) {
            Text("Volver")
        }
    }

    @Preview1(showBackground = true)
    @Composable
    fun WineAppPreview() {
        WineApplicationTheme {
            AppNavigator()
        }
    }
}*/
