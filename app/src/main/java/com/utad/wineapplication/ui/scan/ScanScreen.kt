package com.utad.wineapplication.ui.scan

import android.content.Context
import android.graphics.Bitmap
import android.Manifest
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.utad.wineapplication.data.AppDatabase
import com.utad.wineapplication.data.ScannedText
import com.utad.wineapplication.data.ScannedTextDao
import com.utad.wineapplication.mlkit.MLKitProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: OCRViewModel = viewModel(
        factory = OCRViewModelFactory(
            // Necesitarías inyectar el DAO aquí
            AppDatabase.getDatabase(context).scannedTextDao()
        )
    )

    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    var scannedText by remember { mutableStateOf<String?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            // Corregir el ámbito de la corrutina
            viewModel.viewModelScope.launch(Dispatchers.IO) {
                try {
                    // Convertir el callback a suspending function
                    val textResult = suspendCoroutine<String> { continuation ->
                        MLKitProcessor.processImage(it) { text ->
                            continuation.resumeWith(Result.success(text))
                        }
                    }

                    val imageUri = saveImage(context, it)
                    viewModel.insertScannedText(imageUri, textResult)

                    // Actualizar UI en el hilo principal
                    withContext(Dispatchers.Main) {
                        scannedText = textResult
                        navController.popBackStack()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error al procesar: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } ?: run {
            Toast.makeText(context, "No se tomó ninguna foto", Toast.LENGTH_SHORT).show()
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
            Button(onClick = { takePictureLauncher.launch(null) }) {
                Text("Capturar imagen")
            }

            scannedText?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Texto escaneado:")
                Spacer(modifier = Modifier.height(8.dp))
                Text(it)
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
    val file = File(context.externalCacheDir, "image_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        it.flush()
    }
    return file.absolutePath
}

class OCRViewModel(private val scannedTextDao: ScannedTextDao) : ViewModel() {
    fun insertScannedText(imageUri: String, text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            scannedTextDao.insert(ScannedText(imageUri = imageUri, extractedText = text))
        }
    }
}

class OCRViewModelFactory(private val dao: ScannedTextDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OCRViewModel(dao) as T
    }
}