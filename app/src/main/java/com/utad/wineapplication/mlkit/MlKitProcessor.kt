package com.utad.wineapplication.mlkit

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions


object MLKitProcessor {
    fun processImage(bitmap: Bitmap, callback: (String) -> Unit) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val image = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                callback(visionText.text)
            }
            .addOnFailureListener { e ->
                Log.e("MLKit", "Error al reconocer texto", e)
                callback("Error al reconocer texto")
            }
    }
}