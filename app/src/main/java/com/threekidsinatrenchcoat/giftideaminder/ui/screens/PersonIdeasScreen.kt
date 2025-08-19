package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.GiftViewModel
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.SmsAnalysisCache
import com.threekidsinatrenchcoat.giftideaminder.ui.components.SuggestionsCarousel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton

@Composable
fun PersonIdeasScreen(
    personId: Int,
    navController: NavController,
    smsAnalysis: Boolean = false,
    viewModel: GiftViewModel = hiltViewModel()
) {
    val suggestions by viewModel.suggestions.collectAsState()
    val isLoading by viewModel.isLoadingSuggestions.collectAsState()
    val error by viewModel.suggestionsError.collectAsState()
    val peopleMap by viewModel.peopleById.collectAsState()

    LaunchedEffect(personId, smsAnalysis) {
        if (smsAnalysis) {
            // Get SMS-based suggestions from cache and store in ViewModel
            val smsBasedSuggestions = SmsAnalysisCache.getSuggestions(personId)
            if (smsBasedSuggestions.isNotEmpty()) {
                viewModel.setSmsGiftSuggestions(personId, smsBasedSuggestions)
            }
            // Load SMS-based suggestions 
            viewModel.loadSmsBasedSuggestions(personId)
        } else {
            // Fetch regular AI suggestions for this person
            viewModel.fetchSuggestionsForPerson(personId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(if (smsAnalysis) "SMS Gift Ideas" else "Gift Ideas")
                        peopleMap[personId]?.let { name ->
                            Text(
                                text = "for $name" + if (smsAnalysis) " (from messages)" else "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    // Navigate to person detail screen to add interests instead of add gift
                    navController.navigate("person_detail/$personId") 
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Filled.Add, 
                    contentDescription = "Add Interest",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Main content area with suggestions
            SuggestionsCarousel(
                suggestions = viewModel.suggestions,
                onAccept = { suggestion -> viewModel.insertGift(suggestion.copy(id = 0)) },
                onDismiss = { suggestion -> viewModel.dismissSuggestion(suggestion) },
                isLoading = viewModel.isLoadingSuggestions,
                error = viewModel.suggestionsError,
                personIdToName = peopleMap,
                showDebugPrompt = viewModel.showDebugPrompts.collectAsState().value,
                aiPrompt = viewModel.currentAiPrompt.collectAsState().value,
                isRetrying = viewModel.isRetrying,
                currentRetryCount = viewModel.currentRetryCount
            )
            
            // Bottom section with help text and actions
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "💡 Tip: Swipe to explore all suggestions",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { 
                                if (smsAnalysis) {
                                    viewModel.loadSmsBasedSuggestions(personId)
                                } else {
                                    viewModel.fetchSuggestionsForPerson(personId)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (smsAnalysis) "Reload SMS Ideas" else "Refresh Ideas")
                        }
                        
                        Button(
                            onClick = { navController.navigate("person_detail/$personId") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Add Interests")
                        }
                    }
                }
            }
        }
    }
}

