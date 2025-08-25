package com.threekidsinatrenchcoat.giftideaminder.ui.screens.interests

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Skip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.CategoryQuestion
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.QuestionCategory
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.TwentyQuestionsViewModel

@Composable
fun CategoryQuestionScreen(
    onNavigateToReview: () -> Unit,
    onBack: () -> Unit,
    viewModel: TwentyQuestionsViewModel = hiltViewModel()
) {
    val selectedCategories by viewModel.selectedCategories.collectAsStateWithLifecycle()
    val currentCategoryIndex by viewModel.currentCategoryIndex.collectAsStateWithLifecycle()
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
    val currentStep by viewModel.currentStep.collectAsStateWithLifecycle()
    
    val currentCategory = viewModel.getCurrentCategory()
    val currentQuestions = viewModel.getCurrentQuestions()
    val currentQuestion = currentQuestions.getOrNull(currentQuestionIndex)
    
    // Navigate to review when questions are complete
    LaunchedEffect(currentStep) {
        if (currentStep == 2) {
            onNavigateToReview()
        }
    }
    
    if (currentCategory == null || currentQuestion == null) {
        // Loading or error state
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Progress indicator
        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            LinearProgressIndicator(
                progress = {
                    val totalQuestions = selectedCategories.sumOf { 
                        viewModel.getQuestionsForCategory(it.id).size 
                    }
                    val completedQuestions = currentCategoryIndex * 3 + currentQuestionIndex
                    if (totalQuestions > 0) completedQuestions.toFloat() / totalQuestions else 0f
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Category ${currentCategoryIndex + 1} of ${selectedCategories.size}: ${currentCategory.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "Question ${currentQuestionIndex + 1} of ${currentQuestions.size}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Category header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = currentCategory.emoji,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(end = 12.dp)
            )
            
            Column {
                Text(
                    text = currentCategory.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = currentCategory.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Question
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = currentQuestion.question,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // Answer options
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(currentQuestion.options) { option ->
                Card(
                    onClick = { viewModel.answerQuestion(option) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "Select",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Stop")
            }
            
            OutlinedButton(
                onClick = { viewModel.skipCurrentQuestion() },
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Skip,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text("Skip")
                }
            }
            
            Button(
                onClick = { viewModel.goToReview() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Review")
            }
        }
    }
}