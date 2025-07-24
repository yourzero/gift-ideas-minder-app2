package com.giftideaminder.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.giftideaminder.viewmodel.ImportViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import com.google.android.gms.tasks.Task
import java.io.IOException
import androidx.compose.ui.tooling.preview.Preview



@Preview
@Composable
fun ImportScreen(navController: NavController) {
    val viewModel: ImportViewModel = hiltViewModel()
    val context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { nonNullUri ->
            try {
                val image = InputImage.fromFilePath(context, nonNullUri)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        viewModel.parseOcrTextToGifts(visionText.text)
                    }
            } catch (e: IOException) {
                // Handle error
            }
        }
    }

    val csvPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { nonNullUri ->
            context.contentResolver.openInputStream(nonNullUri)?.use { inputStream ->
                val csvText = inputStream.bufferedReader().readText()
                viewModel.importFromCsv(csvText)
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { imagePicker.launch("image/*") }) {
            Text("Import from Screenshot (OCR)")
        }
        Button(onClick = { csvPicker.launch("text/csv") }) {
            Text("Import from CSV")
        }
        Button(onClick = { viewModel.extractFromSms() }) {
            Text("Extract from SMS")
        }
    }
} 