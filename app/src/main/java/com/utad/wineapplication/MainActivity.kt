package com.utad.wineapplication

import com.utad.wineapplication.viewmodels.WineListViewModel
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import com.utad.wineapplication.data.AppDatabase
import com.utad.wineapplication.data.ScannedText
import com.utad.wineapplication.viewmodels.WineListViewModelFactory
import coil.compose.rememberImagePainter
import com.utad.wineapplication.data.Wine

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
/*@Composable
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
}*/
@Composable
fun WineListScreen(navController: NavController) {
    val context = LocalContext.current
    val scannedTextDao = AppDatabase.getDatabase(context).scannedTextDao()
    val viewModelFactory = WineListViewModelFactory(scannedTextDao)
    val viewModel: WineListViewModel = viewModel(factory = viewModelFactory)

    // Recoge todos los items (vinos predefinidos + escaneados)
    val allItems by viewModel.allItems.collectAsState(initial = emptyList())

    // Estados para el diálogo de edición (solo para ScannedText)
    var showDialog by remember { mutableStateOf(false) }
    var currentItem by remember { mutableStateOf<ScannedText?>(null) }
    var newText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .padding(16.dp)
    ) {
        Text("Nuestros Vinos", style = MaterialTheme.typography.headlineMedium)

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(allItems) { item ->
                when (item) {
                    is Wine -> WineItem(wine = item)
                    is ScannedText -> ScannedItem(
                        scanned = item,
                        onEdit = {
                            currentItem = item
                            newText = item.extractedText
                            showDialog = true
                        },
                        onDelete = { viewModel.deleteItem(item) }
                    )
                }
            }
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Volver")
        }
    }

    // Diálogo para editar textos escaneados
    if (showDialog && currentItem != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    currentItem?.let {
                        viewModel.updateItem(it.copy(extractedText = newText))
                    }
                    showDialog = false
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Editar texto") },
            text = {
                OutlinedTextField(
                    value = newText,
                    onValueChange = { newText = it },
                    singleLine = false,
                    label = { Text("Nuevo texto") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }
}

// Añade estos composables al mismo archivo (MainActivity.kt)

@Composable
fun WineItem(wine: Wine) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = painterResource(id = wine.imageResource),
                contentDescription = "Imagen de ${wine.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(wine.name, style = MaterialTheme.typography.headlineSmall)
            Text(wine.region, style = MaterialTheme.typography.bodyMedium)
            Text(wine.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ScannedItem(
    scanned: ScannedText,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (scanned.imageUri.isNotEmpty()) {
                Image(
                    painter = rememberImagePainter(scanned.imageUri),
                    contentDescription = "Imagen escaneada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 8.dp)
                )
            }
            Text(text = scanned.extractedText)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Text("Editar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDelete) {
                    Text("Borrar")
                }
            }
        }
    }
}


