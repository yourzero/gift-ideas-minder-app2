package com.threekidsinatrenchcoat.giftideaminder.ui.screens.interests

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.QuestionAnswer
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.QuestionCategory
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.TwentyQuestionsViewModel

@Composable
fun TwentyQuestionsReviewScreen(
    onComplete: () -> Unit,
    onStartOver: () -> Unit,
    viewModel: TwentyQuestionsViewModel = hiltViewModel()
) {
    val answers by viewModel.answers.collectAsStateWithLifecycle()
    val isComplete by viewModel.isComplete.collectAsStateWithLifecycle()
    val selectedCategories by viewModel.selectedCategories.collectAsStateWithLifecycle()
    
    LaunchedEffect(isComplete) {
        if (isComplete) {
            onComplete()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Review Your Answers",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Based on your responses, we'll create new interests for you to explore. You can always edit or delete them later.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Answers grouped by category
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            selectedCategories.forEach { category ->
                val categoryAnswers = answers.filter { it.categoryId == category.id }
                
                item(key = category.id) {
                    CategoryAnswersCard(
                        category = category,
                        answers = categoryAnswers,
                        viewModel = viewModel
                    )
                }
            }
        }
        
        // Bottom actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onStartOver,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text("Start Over")
                }
            }
            
            Button(
                onClick = { viewModel.finishFlow() },
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Create Interests")
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryAnswersCard(
    category: QuestionCategory,
    answers: List<QuestionAnswer>,
    viewModel: TwentyQuestionsViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Category header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = category.emoji,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(end = 12.dp)
                )
                
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            // Answers
            answers.forEach { answer ->
                if (answer.answer != "Skipped") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = answer.question,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        
                        Text(
                            text = answer.answer,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.End,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
            
            // Preview of what will be created
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            
            Text(
                text = "Will create: ${category.name} interest",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            val nonSkippedAnswers = answers.filter { it.answer != "Skipped" }
            if (nonSkippedAnswers.isNotEmpty()) {
                Text(
                    text = "Based on: ${nonSkippedAnswers.map { it.answer }.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}