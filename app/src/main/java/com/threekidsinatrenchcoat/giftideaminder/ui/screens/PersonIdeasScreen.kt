package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.GiftViewModel
import com.threekidsinatrenchcoat.giftideaminder.ui.components.SuggestionsCarousel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@Composable
fun PersonIdeasScreen(
    personId: Int,
    navController: NavController,
    viewModel: GiftViewModel = hiltViewModel()
) {
    val suggestions by viewModel.suggestions.collectAsState()
    val isLoading by viewModel.isLoadingSuggestions.collectAsState()
    val error by viewModel.suggestionsError.collectAsState()
    val peopleMap by viewModel.peopleById.collectAsState()

    LaunchedEffect(personId) {
        // Reuse budget flow but with personId hint; or expose a dedicated VM method if needed
        viewModel.fetchSuggestionsByBudget(min = 0.0, max = 10_000.0, personId = personId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gift Ideas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Ideas for ${peopleMap[personId] ?: "Recipient"}", style = MaterialTheme.typography.titleMedium)
            SuggestionsCarousel(
                suggestions = viewModel.suggestions,
                onAccept = { suggestion -> viewModel.insertGift(suggestion.copy(id = 0)) },
                onDismiss = { suggestion -> viewModel.dismissSuggestion(suggestion) },
                isLoading = viewModel.isLoadingSuggestions,
                error = viewModel.suggestionsError,
                personIdToName = peopleMap
            )
            Button(onClick = { viewModel.fetchSuggestionsByBudget(min = 0.0, max = 10_000.0, personId = personId) }) {
                Text("Refresh")
            }
        }
    }
}

