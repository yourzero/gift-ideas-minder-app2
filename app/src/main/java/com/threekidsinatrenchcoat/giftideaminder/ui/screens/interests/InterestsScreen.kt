package com.threekidsinatrenchcoat.giftideaminder.ui.screens.interests

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.threekidsinatrenchcoat.giftideaminder.data.model.InterestEntity
import com.threekidsinatrenchcoat.giftideaminder.ui.components.InterestRow
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.InterestsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestsScreen(
    personId: Long,
    viewModel: InterestsViewModel = hiltViewModel()
) {
    val parentInterests by viewModel.getParentInterests(personId).collectAsStateWithLifecycle(emptyList())
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedInterest by remember { mutableStateOf<InterestEntity?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(personId) {
        viewModel.setPersonId(personId)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Interests",
                style = MaterialTheme.typography.headlineMedium
            )
            
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Interest")
            }
        }
        
        if (parentInterests.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "No interests yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Add some interests to get started",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(parentInterests) { interest ->
                    InterestRow(
                        interest = interest,
                        onInterestClick = {
                            selectedInterest = interest
                            showBottomSheet = true
                        },
                        onToggleOwned = { viewModel.toggleOwned(interest.id, !interest.isOwned) },
                        onToggleDislike = { viewModel.toggleDislike(interest.id, !interest.isDislike) },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
    
    // Add Interest Dialog
    if (showAddDialog) {
        AddInterestDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, description ->
                viewModel.addParentInterest(personId, name, description)
                showAddDialog = false
            }
        )
    }
    
    // Interest Details Bottom Sheet
    if (showBottomSheet && selectedInterest != null) {
        InterestDetailsSheet(
            interest = selectedInterest!!,
            onDismiss = { 
                showBottomSheet = false
                selectedInterest = null
            },
            viewModel = viewModel
        )
    }
}

@Composable
private fun AddInterestDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, description: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Interest") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Interest Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(name.trim(), description.takeIf { it.isNotBlank() }?.trim())
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}