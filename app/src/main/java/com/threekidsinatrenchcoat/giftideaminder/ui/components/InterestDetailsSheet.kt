package com.threekidsinatrenchcoat.giftideaminder.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.threekidsinatrenchcoat.giftideaminder.data.entity.InterestEntity
import com.threekidsinatrenchcoat.giftideaminder.ui.theme.GiftIdeaMinder_androidTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestDetailsSheet(
    parentInterest: InterestEntity,
    childInterests: List<InterestEntity>,
    aiSuggestions: List<String>,
    isLoadingSuggestions: Boolean,
    onDismiss: () -> Unit,
    onToggleOwned: (Int, Boolean) -> Unit,
    onAddDetail: (String) -> Unit,
    onDeleteChild: (InterestEntity) -> Unit,
    onGetSuggestions: (String) -> Unit,
    onAddFromSuggestion: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var newDetailText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            // Header with parent interest and close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = parentInterest.label,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    
                    // Owned toggle button
                    IconButton(
                        onClick = { onToggleOwned(parentInterest.id, !parentInterest.owned) }
                    ) {
                        Text(
                            text = if (parentInterest.owned) "❤️" else "🚫",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
                
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Child details section
            if (childInterests.isNotEmpty()) {
                Text(
                    text = "Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp) // Fixed height to prevent scrolling conflicts
                ) {
                    items(childInterests) { child ->
                        InterestChip(
                            interest = child,
                            onToggleOwned = { onToggleOwned(child.id, !child.owned) },
                            onDelete = { onDeleteChild(child) },
                            showDelete = true
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Add new detail section
            Text(
                text = "Add New Detail",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newDetailText,
                    onValueChange = { newDetailText = it },
                    label = { Text("Interest detail") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (newDetailText.isNotBlank()) {
                                onAddDetail(newDetailText.trim())
                                newDetailText = ""
                                keyboardController?.hide()
                            }
                        }
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                
                IconButton(
                    onClick = {
                        if (newDetailText.isNotBlank()) {
                            onAddDetail(newDetailText.trim())
                            newDetailText = ""
                            keyboardController?.hide()
                        }
                    },
                    enabled = newDetailText.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add detail"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // AI Suggestions section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI Suggestions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                if (!isLoadingSuggestions && aiSuggestions.isEmpty()) {
                    OutlinedButton(
                        onClick = { onGetSuggestions(parentInterest.label) }
                    ) {
                        Text("Get Suggestions")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            when {
                isLoadingSuggestions -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                aiSuggestions.isNotEmpty() -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp) // Fixed height for suggestions
                    ) {
                        items(aiSuggestions) { suggestion ->
                            SuggestionChip(
                                suggestion = suggestion,
                                onClick = { onAddFromSuggestion(suggestion) }
                            )
                        }
                    }
                }
                
                else -> {
                    Text(
                        text = "No suggestions yet. Click 'Get Suggestions' to get AI-powered ideas!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            
            // Bottom padding for navigation bar
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun InterestChip(
    interest: InterestEntity,
    onToggleOwned: () -> Unit,
    onDelete: () -> Unit,
    showDelete: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (interest.owned) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = interest.label,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                
                Row {
                    // Owned toggle
                    IconButton(
                        onClick = onToggleOwned,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Text(
                            text = if (interest.owned) "❤️" else "🚫",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    // Delete button
                    if (showDelete) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestionChip(
    suggestion: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = suggestion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Preview
@Composable
private fun InterestDetailsSheetPreview() {
    GiftIdeaMinder_androidTheme {
        InterestDetailsSheet(
            parentInterest = InterestEntity(
                id = 1,
                personId = 1,
                label = "Gaming",
                parentInterestId = null,
                owned = true
            ),
            childInterests = listOf(
                InterestEntity(
                    id = 2,
                    personId = 1,
                    label = "FPS Games",
                    parentInterestId = 1,
                    owned = true
                ),
                InterestEntity(
                    id = 3,
                    personId = 1,
                    label = "Racing Games",
                    parentInterestId = 1,
                    owned = false
                )
            ),
            aiSuggestions = listOf("Strategy Games", "Indie Games", "Mobile Games"),
            isLoadingSuggestions = false,
            onDismiss = {},
            onToggleOwned = { _, _ -> },
            onAddDetail = {},
            onDeleteChild = {},
            onGetSuggestions = {},
            onAddFromSuggestion = {}
        )
    }
}