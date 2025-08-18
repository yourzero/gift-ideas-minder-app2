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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailScreen(
    personId: Int,
    navController: NavController,
    viewModel: PersonViewModel = hiltViewModel()
) {
    val person by viewModel.getPersonById(personId).collectAsState(initial = null)
    val interests by viewModel.getInterestsForPerson(personId).collectAsState(initial = emptyList())
    
    var selectedType by remember { mutableStateOf(InterestType.GENERAL) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newInterestText by remember { mutableStateOf("") }
    
    // Filter interests by selected type
    val filteredInterests = interests.filter { it.type == selectedType }
    
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
                        Text(
                            text = "Interests & Inspirations",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
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
                                onDelete = { viewModel.deleteInterest(interest) }
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
            title = { Text("Add ${selectedType.name.lowercase().replaceFirstChar { it.uppercase() }} Interest") },
            text = {
                OutlinedTextField(
                    value = newInterestText,
                    onValueChange = { newInterestText = it },
                    label = { Text("Interest") },
                    placeholder = { 
                        Text(
                            if (selectedType == InterestType.GENERAL) {
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
                            viewModel.addInterest(
                                personId = personId,
                                type = selectedType,
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

@Composable
private fun InterestItem(
    interest: Interest,
    onToggleOwned: () -> Unit,
    onDelete: () -> Unit,
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
                if (interest.type == InterestType.SPECIFIC) {
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
                // Toggle owned button (only for specific items)
                if (interest.type == InterestType.SPECIFIC) {
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