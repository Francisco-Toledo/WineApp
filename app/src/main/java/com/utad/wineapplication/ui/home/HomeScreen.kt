package com.utad.wineapplication.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.core.net.toUri
import com.utad.wineapplication.data.ScannedText
import java.io.File
import coil.compose.rememberAsyncImagePainter

@Composable
fun ScannedItem(scannedText: ScannedText) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mostrar imagen escaneada
            Image(
                painter = rememberAsyncImagePainter(File(scannedText.imageUri).toUri()),
                contentDescription = "Imagen escaneada",
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Texto escaneado
            Column {
                Text(text = "Texto:", style = MaterialTheme.typography.labelSmall)
                Text(text = scannedText.extractedText, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
