package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.threekidsinatrenchcoat.giftideaminder.ui.components.GiftItem
import com.threekidsinatrenchcoat.giftideaminder.ui.components.SuggestionsCarousel
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.GiftViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.threekidsinatrenchcoat.giftideaminder.data.model.Gift
import androidx.compose.material3.HorizontalDivider


@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Preview
@Composable
fun GiftListScreen(viewModel: GiftViewModel = hiltViewModel(),
                   navController: NavController) {
    val giftsState = viewModel.allGifts.collectAsState(initial = emptyList<Gift>())
    val gifts = giftsState.value
    var searchQuery by remember { mutableStateOf("") }
    val filteredGifts: List<Gift> = gifts.filter { it.title.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gift Idea Minder") },
                actions = {
                    IconButton(onClick = { /* TODO: Settings */ }) {
                        //Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_gift") }) {
                //Icon(Icons.Filled.Add, contentDescription = "Add New Gift")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text("Quick Actions", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
                // Replace Row with FlowRow for better wrapping on small screens
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = { navController.navigate("add_gift") }) {
                        Text("Add Gift")
                    }
                    Button(onClick = { navController.navigate("person_list") }) {
                        Text("Persons")
                    }
                    Button(onClick = { navController.navigate("import") }) {
                        Text("Import")
                    }
                    Button(onClick = { navController.navigate("budget") }) {
                        Text("Budget")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Text("Search Gifts", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Gifts") },
                    leadingIcon = { //Icon(Icons.Filled.Search, contentDescription = "Search") }
                    },
                    modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Search field" }
                )
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Today’s Suggestions", style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = { viewModel.fetchSuggestions() }) {
                        //Icon(Icons.Filled.Refresh, contentDescription = "Refresh Suggestions")
                    }
                }
                AnimatedVisibility(visible = true) {
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
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Text("Your Gifts", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            }
            if (filteredGifts.size == 0) {
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No gifts yet. Start adding some!", textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { navController.navigate("add_gift") }) {
                            Text("Add Your First Gift")
                        }
                    }
                }
            } else {
                items(
                    items = filteredGifts,
                    key = { giftItem: Gift -> giftItem.id }
                ) { gift ->
                    GiftItem(gift = gift) {
                        navController.navigate("gift_detail/${gift.id}")
                    }
                }
            }
        }
    }
} 