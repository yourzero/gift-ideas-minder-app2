//package com.threekidsinatrenchcoat.giftideaminder.ui.screens
//
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.CenterAlignedTopAppBar
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBarDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import com.threekidsinatrenchcoat.giftideaminder.viewmodel.ImportViewModel
//import com.google.mlkit.vision.common.InputImage
//import com.google.mlkit.vision.text.TextRecognition
//import com.google.mlkit.vision.text.latin.TextRecognizerOptions
//import com.threekidsinatrenchcoat.giftideaminder.ui.components.AppTopBar
//import com.threekidsinatrenchcoat.giftideaminder.viewmodel.ImportViewModel
//import java.io.IOException
//import androidx.compose.ui.tooling.preview.Preview
//
//
//
//@Preview
//@Composable
//fun ImportScreen(
//    navController: NavController,
//    viewModel: ImportViewModel = hiltViewModel()
//) {
//    val context = LocalContext.current
//
//    val imagePicker = rememberLauncherForActivityResult(
//        ActivityResultContracts.GetContent()
//    ) { uri ->
//        uri?.let { nonNullUri ->
//            try {
//                val image = InputImage.fromFilePath(context, nonNullUri)
//                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
//                recognizer.process(image)
//                    .addOnSuccessListener { visionText ->
//                        viewModel.parseOcrTextToGifts(visionText.text)
//                    }
//            } catch (e: IOException) {
//                // Handle error (log, snackbar, etc.)
//            }
//        }
//    }
//
//    val csvPicker = rememberLauncherForActivityResult(
//        ActivityResultContracts.GetContent()
//    ) { uri ->
//        uri?.let { nonNullUri ->
//            context.contentResolver.openInputStream(nonNullUri)?.use { inputStream ->
//                val csvText = inputStream.bufferedReader().readText()
//                viewModel.importFromCsv(csvText)
//            }
//        }
//    }
//
//    Scaffold(
//        topBar = { AppTopBar("Import Ideas") }
//    ) { innerPadding ->
//        ImportScreenContent(
//            modifier = Modifier
//                .padding(innerPadding)
//                .padding(16.dp),
//            onPickImage = { imagePicker.launch("image/*") },
//            onPickCsv = { csvPicker.launch("text/csv") },
//            onExtractSms = { viewModel.extractFromSms() }
//        )
//    }
//}
//
//@Composable
//private fun ImportScreenContent(
//    modifier: Modifier = Modifier,
//    onPickImage: () -> Unit,
//    onPickCsv: () -> Unit,
//    onExtractSms: () -> Unit
//) {
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//    ) {
//        Button(onClick = onPickImage) {
//            Text("Import from Screenshot (OCR)")
//        }
//        Button(onClick = onPickCsv, modifier = Modifier.padding(top = 12.dp)) {
//            Text("Import from CSV")
//        }
//        Button(onClick = onExtractSms, modifier = Modifier.padding(top = 12.dp)) {
//            Text("Extract from SMS")
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//private fun ImportScreenPreview() {
//    Scaffold(
//        topBar = { AppTopBar("Import Ideas") }
//    ) { innerPadding ->
//        ImportScreenContent(
//            modifier = Modifier
//                .padding(innerPadding)
//                .padding(16.dp),
//            onPickImage = {},
//            onPickCsv = {},
//            onExtractSms = {}
//        )
//    }
//}
