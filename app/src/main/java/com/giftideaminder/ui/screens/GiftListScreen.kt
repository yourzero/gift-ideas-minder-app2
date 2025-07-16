package com.giftideaminder.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.giftideaminder.ui.components.GiftItem
import com.giftideaminder.ui.components.SuggestionsCarousel
import com.giftideaminder.viewmodel.GiftViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiftListScreen(viewModel: GiftViewModel, navController: NavController) {
    val gifts = viewModel.allGifts.collectAsState(initial = emptyList()).value
    var searchQuery by remember { mutableStateOf("") }
    val filteredGifts = gifts.filter { it.title.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gift Idea Minder") },
                actions = {
                    IconButton(onClick = { /* TODO: Settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_gift") }) {
                Icon(Icons.Default.Add, contentDescription = "Add New Gift")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { navController.navigate("add_gift") }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Gift")
                        Text("Add Gift")
                    }
                    Button(onClick = { navController.navigate("person_list") }) {
                        Icon(Icons.Default.Person, contentDescription = "Manage Persons")
                        Text("Persons")
                    }
                    Button(onClick = { navController.navigate("import") }) {
                        Text("Import")
                    }
                    Button(onClick = { navController.navigate("budget") }) {
                        Text("Budget")
                    }
                }
            }
            item {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Gifts") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Search field" }
                )
            }
            item {
                AnimatedVisibility(visible = true) {  // Can tie to suggestions availability
                    SuggestionsCarousel(
                        suggestions = viewModel.suggestions,
                        onAccept = { suggestion ->
                            viewModel.insertGift(suggestion.copy(id = 0))
                        },
                        onDismiss = { suggestion ->
                            viewModel.dismissSuggestion(suggestion)
                        }
                    )
                }
            }
            if (filteredGifts.isEmpty()) {
                item {
                    Text("No gifts yet. Add some!")
                }
            } else {
                items(
                    items = filteredGifts,
                    key = { it.id }
                ) { gift ->
                    GiftItem(gift = gift) {
                        navController.navigate("gift_detail/${gift.id}")
                    }
                }
            }
        }
    }
} 