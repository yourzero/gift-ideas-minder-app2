package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.threekidsinatrenchcoat.giftideaminder.data.model.Interest
import com.threekidsinatrenchcoat.giftideaminder.data.model.InterestType
import com.threekidsinatrenchcoat.giftideaminder.data.model.Person
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.PersonViewModel
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailScreen(
    personId: Int,
    navController: NavController,
    viewModel: PersonViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val person by viewModel.getPersonById(personId).collectAsState(initial = null)
    val interests by viewModel.getInterestsForPerson(personId).collectAsState(initial = emptyList())
    val isAdvancedMode by settingsViewModel.isAdvancedMode.collectAsState()
    
    // Force General mode if not in advanced mode
    var selectedType by remember(isAdvancedMode) { 
        mutableStateOf(if (isAdvancedMode) InterestType.GENERAL else InterestType.GENERAL) 
    }
    var showAddDialog by remember { mutableStateOf(false) }
    var newInterestText by remember { mutableStateOf("") }
    
    // Track specific-sounding interests added in simple mode
    var showAdvancedModePrompt by remember { mutableStateOf(false) }
    
    // Check if we should show advanced mode prompt
    LaunchedEffect(interests, isAdvancedMode) {
        if (!isAdvancedMode) {
            // Count interests that sound specific (contain specific keywords or patterns)
            val specificSoundingInterests = interests.count { interest ->
                isSpecificSounding(interest.value)
            }
            
            // Show prompt if 3+ specific-sounding interests in simple mode
            if (specificSoundingInterests >= 3) {
                showAdvancedModePrompt = true
            }
        }
    }
    
    // Filter interests by selected type and mode
    val filteredInterests = interests.filter { 
        if (!isAdvancedMode) {
            // Simple mode: only show general interests
            it.type == InterestType.GENERAL 
        } else {
            // Advanced mode: show selected type
            it.type == selectedType
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
                
                // Interest type toggle
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Interests & Inspirations",
                                style = MaterialTheme.typography.titleLarge
                            )
                            
                            if (!isAdvancedMode) {
                                TextButton(
                                    onClick = { navController.navigate("settings") }
                                ) {
                                    Text("Enable Advanced Mode")
                                }
                            }
                        }
                        
                        if (!isAdvancedMode) {
                            Text(
                                text = "Simple mode: Only general interests (cooking, sports, music)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        } else {
                            // Type toggle buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    onClick = { selectedType = InterestType.GENERAL },
                                    label = { Text("General") },
                                    selected = selectedType == InterestType.GENERAL,
                                    modifier = Modifier.weight(1f)
                                )
                                FilterChip(
                                    onClick = { selectedType = InterestType.SPECIFIC },
                                    label = { Text("Specific") },
                                    selected = selectedType == InterestType.SPECIFIC,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
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
                            Text(
                                text = "No ${selectedType.name.lowercase()} interests yet.\nTap + to add some!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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
                                isAdvancedMode = isAdvancedMode
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
                val typeToAdd = if (!isAdvancedMode) InterestType.GENERAL else selectedType
                Text("Add ${typeToAdd.name.lowercase().replaceFirstChar { it.uppercase() }} Interest") 
            },
            text = {
                OutlinedTextField(
                    value = newInterestText,
                    onValueChange = { newInterestText = it },
                    label = { Text("Interest") },
                    placeholder = { 
                        val typeToAdd = if (!isAdvancedMode) InterestType.GENERAL else selectedType
                        Text(
                            if (typeToAdd == InterestType.GENERAL) {
                                "e.g., cooking, sports, music"
                            } else {
                                "e.g., Nike Air Max shoes, iPhone 15"
                            }
                        ) 
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newInterestText.isNotBlank()) {
                            val typeToAdd = if (!isAdvancedMode) InterestType.GENERAL else selectedType
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
    
    // Advanced mode suggestion prompt
    if (showAdvancedModePrompt) {
        AlertDialog(
            onDismissRequest = { showAdvancedModePrompt = false },
            title = { Text("Enable Advanced Mode?") },
            text = {
                Column {
                    Text("It looks like you're adding specific items like product names or brands.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Advanced Mode lets you:")
                    Text("• Separate general interests from specific items")
                    Text("• Mark specific items as 'already owned'")
                    Text("• Get better AI suggestions")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        settingsViewModel.setAdvancedMode(true)
                        showAdvancedModePrompt = false
                    }
                ) {
                    Text("Enable Advanced Mode")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAdvancedModePrompt = false }
                ) {
                    Text("Keep Simple Mode")
                }
            }
        )
    }
}

/**
 * Checks if an interest value sounds specific (like a product name or brand)
 * rather than general (like "cooking" or "sports")
 */
private fun isSpecificSounding(interest: String): Boolean {
    val lowercaseInterest = interest.lowercase().trim()
    
    // Check for specific patterns that indicate specific items
    return when {
        // Contains brand names or specific product indicators
        lowercaseInterest.contains("nike") || 
        lowercaseInterest.contains("adidas") ||
        lowercaseInterest.contains("apple") ||
        lowercaseInterest.contains("samsung") ||
        lowercaseInterest.contains("iphone") ||
        lowercaseInterest.contains("macbook") ||
        lowercaseInterest.contains("playstation") ||
        lowercaseInterest.contains("xbox") ||
        lowercaseInterest.contains("switch") -> true
        
        // Contains model numbers or specific product patterns
        lowercaseInterest.matches(Regex(".*\\d+.*")) && 
        (lowercaseInterest.contains("max") || 
         lowercaseInterest.contains("pro") ||
         lowercaseInterest.contains("plus") ||
         lowercaseInterest.contains("air")) -> true
        
        // Contains specific product categories with descriptors
        lowercaseInterest.contains("shoes") && 
        (lowercaseInterest.contains(" ") && lowercaseInterest.length > 10) -> true
        
        // Long descriptive phrases (likely specific items)
        lowercaseInterest.contains(" ") && lowercaseInterest.length > 15 -> true
        
        // Contains specific size/color/model indicators
        lowercaseInterest.contains("size") ||
        lowercaseInterest.contains("color") ||
        lowercaseInterest.contains("black") ||
        lowercaseInterest.contains("white") ||
        lowercaseInterest.contains("red") ||
        lowercaseInterest.contains("blue") ||
        lowercaseInterest.contains("large") ||
        lowercaseInterest.contains("medium") ||
        lowercaseInterest.contains("small") -> true
        
        else -> false
    }
}

@Composable
private fun InterestItem(
    interest: Interest,
    onToggleOwned: () -> Unit,
    onDelete: () -> Unit,
    isAdvancedMode: Boolean = true,
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
                if (interest.type == InterestType.SPECIFIC && isAdvancedMode) {
                    Text(
                        text = if (interest.alreadyOwned) "Already owned" else "Available",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (interest.alreadyOwned) 
                            MaterialTheme.colorScheme.error 
                        else 
                            MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Row {
                // Toggle owned button (only for specific items in advanced mode)
                if (interest.type == InterestType.SPECIFIC && isAdvancedMode) {
                    TextButton(
                        onClick = onToggleOwned
                    ) {
                        Text(if (interest.alreadyOwned) "Mark Available" else "Mark Owned")
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