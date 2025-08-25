package com.threekidsinatrenchcoat.giftideaminder.ui.screens.interests

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.threekidsinatrenchcoat.giftideaminder.data.model.InterestEntity
import com.threekidsinatrenchcoat.giftideaminder.ui.components.DetailChip
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.InterestsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestDetailsSheet(
    interest: InterestEntity,
    onDismiss: () -> Unit,
    viewModel: InterestsViewModel
) {
    val childInterests by viewModel.getChildInterests(interest.id).collectAsStateWithLifecycle(emptyList())
    val suggestions by viewModel.suggestions.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    
    var showAddDialog by remember { mutableStateOf(false) }
    
    ModalBottomSheet(
        onDismissRequest = {
            viewModel.clearSuggestions()
            onDismiss()
        },
        modifier = Modifier.fillMaxHeight(0.8f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = interest.name,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    if (!interest.description.isNullOrBlank()) {
                        Text(
                            text = interest.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                
                Row {
                    // Get suggestions button
                    IconButton(
                        onClick = { viewModel.generateSuggestions(interest.name) },
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = "Get suggestions"
                            )
                        }
                    }
                    
                    // Add detail button
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add detail")
                    }
                }
            }
            
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            
            // AI Suggestions Section
            if (suggestions.isNotEmpty()) {
                Text(
                    "Suggested Details",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    items(suggestions) { suggestion ->
                        SuggestionChip(
                            onClick = {
                                viewModel.addChildInterest(
                                    parentId = interest.id,
                                    personId = interest.personId,
                                    name = suggestion
                                )
                            },
                            label = { Text(suggestion) }
                        )
                    }
                }
                
                Divider(modifier = Modifier.padding(vertical = 16.dp))
            }
            
            // Current Details Section
            Text(
                "Current Details",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            if (childInterests.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No details yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Details chips in a flexible grid
                Column {
                    childInterests.chunked(2).forEach { rowInterests ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowInterests.forEach { childInterest ->
                                DetailChip(
                                    interest = childInterest,
                                    onToggleOwned = {
                                        viewModel.toggleOwned(childInterest.id, !childInterest.isOwned)
                                    },
                                    onToggleDislike = {
                                        viewModel.toggleDislike(childInterest.id, !childInterest.isDislike)
                                    },
                                    onDelete = {
                                        viewModel.deleteInterest(childInterest)
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // Fill remaining space if odd number of items
                            if (rowInterests.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Add Detail Dialog
    if (showAddDialog) {
        AddDetailDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, description ->
                viewModel.addChildInterest(
                    parentId = interest.id,
                    personId = interest.personId,
                    name = name,
                    description = description
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddDetailDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, description: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Detail") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Detail Name") },
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