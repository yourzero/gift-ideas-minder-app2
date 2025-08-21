package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.threekidsinatrenchcoat.giftideaminder.data.model.Interest
import com.threekidsinatrenchcoat.giftideaminder.data.model.InterestType
import com.threekidsinatrenchcoat.giftideaminder.data.model.Person
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.PersonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailScreen(
    personId: Int,
    navController: NavController,
    viewModel: PersonViewModel = hiltViewModel()
) {
    val person by viewModel.getPersonById(personId).collectAsState(initial = null)
    val interests by viewModel.getInterestsForPerson(personId).collectAsState(initial = emptyList())
    
    // Simple tab-based navigation between interests and wishlist
    var selectedTab by remember { mutableStateOf(0) } // 0 = Interests, 1 = Wishlist
    var showAddDialog by remember { mutableStateOf(false) }
    var newInterestText by remember { mutableStateOf("") }
    var showSuggestionPrompt by remember { mutableStateOf(false) }
    var suggestedTab by remember { mutableStateOf(0) }
    
    // Filter interests based on selected tab
    val filteredInterests = interests.filter { 
        when (selectedTab) {
            0 -> it.type == InterestType.GENERAL  // Interests tab
            1 -> it.type == InterestType.SPECIFIC // Wishlist tab
            else -> true
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(person?.name ?: "Person Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("edit_person/${personId}") }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Interest")
            }
        }
    ) { innerPadding ->
        person?.let { currentPerson ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // Person basic info
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = currentPerson.name,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        currentPerson.birthday?.let { birthday ->
                            Text(
                                text = "Birthday: ${birthday}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // Tab navigation for Interests and Wishlist
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "${person?.name}'s Profile",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // Tab selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                onClick = { selectedTab = 0 },
                                label = { Text("Interests") },
                                selected = selectedTab == 0,
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                onClick = { selectedTab = 1 },
                                label = { Text("Wishlist") },
                                selected = selectedTab == 1,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        // Tab descriptions
                        Text(
                            text = when (selectedTab) {
                                0 -> "General interests like hobbies, activities, and preferences"
                                1 -> "Specific items they want or already own"
                                else -> ""
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
                
                // Interests list
                if (filteredInterests.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = when (selectedTab) {
                                        0 -> "No interests added yet"
                                        1 -> "No wishlist items yet"
                                        else -> "No items yet"
                                    },
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = when (selectedTab) {
                                        0 -> "Add hobbies, activities, or things they enjoy"
                                        1 -> "Add specific products or items they want"
                                        else -> "Tap + to add items"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredInterests) { interest ->
                            InterestItem(
                                interest = interest,
                                onToggleOwned = { viewModel.toggleInterestOwned(interest) },
                                onDelete = { viewModel.deleteInterest(interest) },
                                isWishlistTab = selectedTab == 1
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Add interest dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { 
                showAddDialog = false 
                newInterestText = ""
            },
            title = { 
                Text(when (selectedTab) {
                    0 -> "Add Interest"
                    1 -> "Add Wishlist Item"
                    else -> "Add Item"
                })
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = newInterestText,
                        onValueChange = { text -> 
                            newInterestText = text
                            // Check if we should suggest switching tabs
                            val suggestion = getTabSuggestion(text)
                            if (suggestion != selectedTab && text.trim().length > 3) {
                                suggestedTab = suggestion
                                showSuggestionPrompt = true
                            } else {
                                showSuggestionPrompt = false
                            }
                        },
                        label = { Text(when (selectedTab) {
                            0 -> "Interest"
                            1 -> "Wishlist Item"
                            else -> "Item"
                        }) },
                        placeholder = { 
                            Text(when (selectedTab) {
                                0 -> "e.g., cooking, hiking, board games"
                                1 -> "e.g., Nike Air Max, iPhone 15, guitar"
                                else -> "Enter item"
                            })
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Smart suggestion prompt
                    if (showSuggestionPrompt) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "This looks like ${if (suggestedTab == 0) "an interest" else "a specific item"}. Switch to ${if (suggestedTab == 0) "Interests" else "Wishlist"} tab?",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(
                                    onClick = {
                                        selectedTab = suggestedTab
                                        showSuggestionPrompt = false
                                    }
                                ) {
                                    Text("Switch")
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newInterestText.isNotBlank()) {
                            val typeToAdd = when (selectedTab) {
                                0 -> InterestType.GENERAL
                                1 -> InterestType.SPECIFIC
                                else -> InterestType.GENERAL
                            }
                            viewModel.addInterest(
                                personId = personId,
                                type = typeToAdd,
                                value = newInterestText.trim()
                            )
                            showAddDialog = false
                            newInterestText = ""
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showAddDialog = false
                        newInterestText = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Analyzes text to suggest the most appropriate tab (Interests vs Wishlist)
 * Returns 0 for Interests, 1 for Wishlist
 */
private fun getTabSuggestion(text: String): Int {
    val lowercaseText = text.lowercase().trim()
    
    // Specific product indicators suggest Wishlist (tab 1)
    val specificIndicators = listOf(
        // Brand names
        "nike", "adidas", "apple", "samsung", "sony", "microsoft", "google",
        "amazon", "netflix", "spotify", "airpods", "iphone", "ipad", "macbook",
        "playstation", "xbox", "nintendo", "switch", "kindle",
        
        // Product types with specific descriptors
        "pro", "max", "plus", "ultra", "premium", "deluxe", "edition",
        
        // Size/color/model indicators
        "size", "color", "black", "white", "red", "blue", "green", "gold", "silver",
        "large", "medium", "small", "xl", "xxl", "32gb", "64gb", "128gb", "256gb",
        
        // Specific product categories
        "shoes size", "model", "version", "generation", "gb", "tb", "inch", "cm"
    )
    
    // General interest indicators suggest Interests (tab 0)
    val generalIndicators = listOf(
        "love", "enjoy", "like", "interested in", "passion for", "hobby",
        "activity", "sport", "music", "art", "reading", "writing", "cooking",
        "traveling", "hiking", "running", "swimming", "dancing", "singing",
        "gaming", "movies", "books", "photography", "gardening", "crafting"
    )
    
    // Check for specific indicators first (higher priority)
    val hasSpecificIndicators = specificIndicators.any { lowercaseText.contains(it) }
    val hasGeneralIndicators = generalIndicators.any { lowercaseText.contains(it) }
    
    // Additional checks for specific items
    val hasNumbers = lowercaseText.matches(Regex(".*\\d+.*"))
    val hasSpecificPhrases = lowercaseText.length > 20 && lowercaseText.contains(" ")
    val hasCurrency = lowercaseText.contains("$") || lowercaseText.contains("dollar") || lowercaseText.contains("price")
    
    return when {
        hasSpecificIndicators -> 1 // Wishlist
        hasCurrency -> 1 // Wishlist
        hasNumbers && (lowercaseText.contains("gb") || lowercaseText.contains("inch") || lowercaseText.contains("size")) -> 1 // Wishlist
        hasGeneralIndicators -> 0 // Interests
        hasSpecificPhrases -> 1 // Long descriptive phrases are likely specific items
        lowercaseText.split(" ").size == 1 && lowercaseText.length < 15 -> 0 // Single words are likely general interests
        else -> 0 // Default to interests for ambiguous cases
    }
}


@Composable
private fun InterestItem(
    interest: Interest,
    onToggleOwned: () -> Unit,
    onDelete: () -> Unit,
    isWishlistTab: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = interest.value,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (isWishlistTab && interest.type == InterestType.SPECIFIC) {
                    Text(
                        text = if (interest.alreadyOwned) "Already owned" else "Want this",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (interest.alreadyOwned) 
                            MaterialTheme.colorScheme.outline 
                        else 
                            MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Row {
                // Toggle owned button (only for wishlist items)
                if (isWishlistTab && interest.type == InterestType.SPECIFIC) {
                    TextButton(
                        onClick = onToggleOwned
                    ) {
                        Text(if (interest.alreadyOwned) "Mark as Want" else "Mark as Owned")
                    }
                }
                
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}