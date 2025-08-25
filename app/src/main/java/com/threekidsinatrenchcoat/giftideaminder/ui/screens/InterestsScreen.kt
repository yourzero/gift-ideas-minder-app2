package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.threekidsinatrenchcoat.giftideaminder.data.entities.InterestEntity
import com.threekidsinatrenchcoat.giftideaminder.ui.components.InterestDetailsSheet
import com.threekidsinatrenchcoat.giftideaminder.ui.viewmodels.InterestsViewModel
import com.threekidsinatrenchcoat.giftideaminder.util.showToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestsScreen(
    personId: Long,
    personName: String,
    navController: NavController,
    viewModel: InterestsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    var showAddInterestDialog by remember { mutableStateOf(false) }
    var selectedInterest by remember { mutableStateOf<InterestEntity?>(null) }
    
    LaunchedEffect(personId) {
        viewModel.loadInterests(personId)
    }
    
    // Handle UI state messages
    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            context.showToast(message)
            viewModel.clearMessage()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "$personName's Interests",
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddInterestDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Interest"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                uiState.parentInterests.isEmpty() -> {
                    EmptyInterestsContent(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = uiState.parentInterests,
                            key = { it.id }
                        ) { interest ->
                            ParentInterestItem(
                                interest = interest,
                                childCount = uiState.childCounts[interest.id] ?: 0,
                                onInterestClick = { selectedInterest = interest },
                                onToggleDislike = { viewModel.toggleDislike(interest.id) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Add Interest Dialog
    if (showAddInterestDialog) {
        AddInterestDialog(
            onDismiss = { showAddInterestDialog = false },
            onAdd = { label ->
                viewModel.addParentInterest(personId, label)
                showAddInterestDialog = false
            }
        )
    }
    
    // Interest Details Bottom Sheet
    selectedInterest?.let { interest ->
        InterestDetailsSheet(
            interest = interest,
            childInterests = uiState.childInterests[interest.id] ?: emptyList(),
            onDismiss = { selectedInterest = null },
            onAddChild = { parentId, label ->
                viewModel.addChildInterest(parentId, label)
            },
            onToggleDislike = { interestId ->
                viewModel.toggleDislike(interestId)
            },
            onDeleteInterest = { interestId ->
                viewModel.deleteInterest(interestId)
                if (interestId == interest.id) {
                    selectedInterest = null
                }
            }
        )
    }
}

@Composable
private fun ParentInterestItem(
    interest: InterestEntity,
    childCount: Int,
    onInterestClick: () -> Unit,
    onToggleDislike: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onInterestClick,
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = interest.label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (interest.isDislike) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                
                if (childCount > 0) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.clip(CircleShape)
                    ) {
                        Text(
                            text = "+$childCount",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            IconButton(
                onClick = onToggleDislike,
                modifier = Modifier.size(40.dp)
            ) {
                Text(
                    text = if (interest.isDislike) "❤️" else "🚫",
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
private fun EmptyInterestsContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "🎯",
            fontSize = 64.sp
        )
        Text(
            text = "No interests yet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Add some interests to get better gift recommendations",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AddInterestDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var label by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Interest",
                fontWeight = FontWeight.Medium
            )
        },
        text = {
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("Interest") },
                placeholder = { Text("e.g., Photography, Cooking") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (label.isNotBlank()) {
                        onAdd(label.trim())
                    }
                },
                enabled = label.isNotBlank()
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