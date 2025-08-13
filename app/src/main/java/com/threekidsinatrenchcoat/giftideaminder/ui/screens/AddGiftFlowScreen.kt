package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Add Gift Flow: pick person → pick date → add gift details
 * Multi-step flow for creating a new gift idea
 */
@Composable
fun AddGiftFlowScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableIntStateOf(1) }
    val totalSteps = 3
    
    val stepTitles = listOf(
        "Pick Person",
        "Pick Date",
        "Gift Details"
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${stepTitles[currentStep - 1]} (${currentStep}/${totalSteps})") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress indicator
            LinearProgressIndicator(
                progress = { currentStep / totalSteps.toFloat() },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(Modifier.height(24.dp))
            
            // Step content
            when (currentStep) {
                1 -> PickPersonStep(
                    onPersonSelected = { currentStep = 2 }
                )
                2 -> PickDateStep(
                    onDateSelected = { currentStep = 3 }
                )
                3 -> GiftDetailsStep(
                    onGiftCreated = { 
                        // Navigate back to gifts screen
                        navController.navigateUp()
                    }
                )
            }
            
            Spacer(Modifier.height(24.dp))
            
            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentStep > 1) {
                    OutlinedButton(
                        onClick = { currentStep-- }
                    ) {
                        Text("Back")
                    }
                } else {
                    Spacer(Modifier.width(1.dp))
                }
                
                if (currentStep < totalSteps) {
                    Button(
                        onClick = { currentStep++ }
                    ) {
                        Text("Next")
                    }
                }
            }
        }
    }
}

@Composable
private fun PickPersonStep(onPersonSelected: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Select who this gift is for",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(16.dp))
        
        // TODO: Replace with actual person list
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            onClick = onPersonSelected
        ) {
            Text(
                "Sample Person",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun PickDateStep(onDateSelected: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "When is this gift for?",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(16.dp))
        
        // TODO: Replace with actual date picker
        Button(onClick = onDateSelected) {
            Text("Pick Date")
        }
    }
}

@Composable
private fun GiftDetailsStep(onGiftCreated: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Add gift details",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(16.dp))
        
        // TODO: Replace with actual gift form
        OutlinedTextField(
            value = "",
            onValueChange = { },
            label = { Text("Gift idea") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(Modifier.height(16.dp))
        
        Button(
            onClick = onGiftCreated,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Gift")
        }
    }
}