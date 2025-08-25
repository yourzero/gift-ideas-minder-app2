package com.threekidsinatrenchcoat.giftideaminder.ui.screens.interests

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.threekidsinatrenchcoat.giftideaminder.ui.components.AppTopBar
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.TwentyQuestionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPickerScreen(
    personId: Int,
    personName: String,
    navController: NavController,
    viewModel: TwentyQuestionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Interest Discovery",
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Text("Skip")
                    }
                }
            )
        },
        floatingActionButton = {
            val completedCategories = getCompletedCategories(uiState.answers)
            if (completedCategories.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { 
                        viewModel.finishFlow(personId)
                        navController.navigateUp()
                    }
                ) {
                    Icon(Icons.Default.Done, contentDescription = "Finish & Save")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "Discover interests for $personName",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            
            Text(
                text = "Select categories to answer questions and build a personalized gift profile",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
            
            // Categories Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(uiState.availableCategories) { category ->
                    CategoryCard(
                        category = category,
                        progress = getCategoryProgress(category, uiState.answers),
                        isComplete = isCategoryComplete(category, uiState.answers),
                        onClick = { 
                            navController.navigate("twenty_questions/$personId/$category")
                        }
                    )
                }
            }
            
            // Progress Summary
            val completedCount = getCompletedCategories(uiState.answers).size
            val totalCount = uiState.availableCategories.size
            
            if (completedCount > 0) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Progress: $completedCount of $totalCount categories complete",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        if (completedCount == totalCount) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "All categories complete! Tap the check button to save.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: String,
    progress: Pair<Int, Int>, // (answered, total)
    isComplete: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isComplete) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Category name
                Text(
                    text = category,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Progress indicator
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isComplete) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Complete",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Complete",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else if (progress.first > 0) {
                        Text(
                            text = "${progress.first}/${progress.second}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "in progress",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "${progress.second} questions",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "tap to start",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Get progress for a specific category (answered questions, total questions)
 */
private fun getCategoryProgress(category: String, answers: Map<String, List<String>>): Pair<Int, Int> {
    val categoryAnswers = answers[category] ?: emptyList()
    val totalQuestions = TwentyQuestionsViewModel.CATEGORY_QUESTIONS[category]?.size ?: 0
    val answeredQuestions = categoryAnswers.count { it.isNotBlank() }
    return Pair(answeredQuestions, totalQuestions)
}

/**
 * Check if a category is complete (all questions answered)
 */
private fun isCategoryComplete(category: String, answers: Map<String, List<String>>): Boolean {
    val progress = getCategoryProgress(category, answers)
    return progress.first == progress.second && progress.second > 0
}

/**
 * Get list of completed categories
 */
private fun getCompletedCategories(answers: Map<String, List<String>>): List<String> {
    return answers.keys.filter { category ->
        isCategoryComplete(category, answers)
    }
}