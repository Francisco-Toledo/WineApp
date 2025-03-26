package com.utad.wineapplication.ui.scan

import android.graphics.Bitmap
import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.utad.wineapplication.data.AppDatabase
import com.utad.wineapplication.mlkit.MLKitProcessor
import com.utad.wineapplication.viewmodels.OCRViewModel
import com.utad.wineapplication.viewmodels.OCRViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.suspendCoroutine

@Composable
fun ScanScreen(navController: NavController) {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).scannedTextDao() }
    val viewModel: OCRViewModel = viewModel(
        factory = OCRViewModelFactory(dao)
    )
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    var showResult by remember { mutableStateOf(false) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            viewModel.viewModelScope.launch(Dispatchers.IO) {
                try {
                    val textResult = suspendCoroutine { continuation ->
                        MLKitProcessor.processImage(it) { text ->
                            continuation.resumeWith(Result.success(text))
                        }
                    }

                    val imageUri = saveImage(context, it)
                    viewModel.insertScannedText(imageUri, textResult)

                    withContext(Dispatchers.Main) {
                        showResult = true
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (permissionState.status.isGranted) {
            if (showResult) {
                // Muestra el último resultado escaneado
                val lastResult = viewModel.scannedTexts.lastOrNull()
                lastResult?.let {
                    Text("Imagen: ${it.imageUri}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Texto reconocido:")
                    Text(it.extractedText)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Volver")
                }
            } else {
                Button(onClick = { takePictureLauncher.launch(null) }) {
                    Text("Capturar imagen")
                }
            }
        } else {
            Text("Permiso de cámara requerido")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { permissionState.launchPermissionRequest() }) {
                Text("Solicitar permiso")
            }
        }
    }
}
private fun saveImage(context: Context, bitmap: Bitmap): String {
    return try {
        // Crea un archivo temporal en el directorio de caché externo
        val file = File.createTempFile(
            "wine_scan_${System.currentTimeMillis()}",
            ".jpg",
            context.externalCacheDir
        )

        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.flush()
        }

        file.absolutePath // Devuelve la ruta completa del archivo guardado
    } catch (e: Exception) {
        e.printStackTrace()
        "" // Devuelve cadena vacía si hay error
    }
}