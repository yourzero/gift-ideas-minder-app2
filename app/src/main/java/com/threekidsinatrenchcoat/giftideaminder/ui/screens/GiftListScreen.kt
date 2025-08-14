package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Divider
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.foundation.layout.Box
import com.threekidsinatrenchcoat.giftideaminder.ui.components.AppTopBar
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.PersonViewModel
import com.threekidsinatrenchcoat.giftideaminder.data.model.Person
import kotlinx.coroutines.flow.MutableStateFlow


@Preview
@Composable
fun GiftListScreen(viewModel: GiftViewModel = hiltViewModel(),
                   navController: NavController) {
    val giftsState = viewModel.allGifts.collectAsState(initial = emptyList<Gift>())
    val personViewModel: PersonViewModel = hiltViewModel()
    val personsState = personViewModel.allPersons.collectAsState(initial = emptyList<Person>())

    val gifts = giftsState.value
    val persons = personsState.value
    var searchQuery by remember { mutableStateOf("") }
    val filteredGifts: List<Gift> = gifts.filter { it.title.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        topBar = { AppTopBar("Gift Idea Minder") },
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
                Text("Upcoming Gifts", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
                val upcomingGifts = gifts.filter { gift -> 
                    gift.eventDate?.let { eventDate ->
                        val currentTime = System.currentTimeMillis()
                        val thirtyDaysFromNow = currentTime + (30 * 24 * 60 * 60 * 1000L)
                        eventDate in currentTime..thirtyDaysFromNow
                    } ?: false
                }
                
                if (upcomingGifts.isEmpty()) {
                    Text("No upcoming gift events in the next 30 days", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 8.dp))
                } else {
                    upcomingGifts.sortedBy { it.eventDate }.forEach { gift ->
                        GiftItem(gift = gift) {
                            navController.navigate("gift_detail/${gift.id}")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (filteredGifts.isEmpty()) {
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
                // Group gifts by person
                val giftsByPerson = filteredGifts.groupBy { it.personId }
                val peopleById = persons.associateBy { it.id }
                
                giftsByPerson.forEach { (personId, giftsForPerson) ->
                    item {
                        val personName = personId?.let { peopleById[it]?.name } ?: "Unassigned"
                        Text(
                            text = "Gifts for $personName", 
                            style = MaterialTheme.typography.titleMedium, 
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        
                        // Show suggestions for this person if they have gifts
                        if (personId != null) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("AI Suggestions", style = MaterialTheme.typography.labelMedium)
                                IconButton(onClick = { 
                                    // Fetch suggestions for this specific person
                                    viewModel.fetchSuggestions()
                                }) {
                                    //Icon(Icons.Filled.Refresh, contentDescription = "Get suggestions for ${personName}")
                                }
                            }
                            
                            // Filter suggestions for this person
                            val suggestionsForPerson = viewModel.suggestions.collectAsState().value.filter { it.personId == personId }
                            if (suggestionsForPerson.isNotEmpty()) {
                                SuggestionsCarousel(
                                    suggestions = MutableStateFlow(suggestionsForPerson),
                                    onAccept = { suggestion ->
                                        viewModel.insertGift(suggestion.copy(id = 0))
                                    },
                                    onDismiss = { suggestion ->
                                        viewModel.dismissSuggestion(suggestion)
                                    },
                                    isLoading = viewModel.isLoadingSuggestions,
                                    error = viewModel.suggestionsError,
                                    personIdToName = viewModel.peopleById.collectAsState().value
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    
                    items(
                        items = giftsForPerson,
                        key = { giftItem: Gift -> giftItem.id }
                    ) { gift ->
                        GiftItem(gift = gift) {
                            navController.navigate("gift_detail/${gift.id}")
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
}
} 