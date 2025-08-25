package com.threekidsinatrenchcoat.giftideaminder.ui.screens.interests

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.threekidsinatrenchcoat.giftideaminder.data.model.InterestAnswer

@Composable
fun TwentyQuestionsFlowNavHost(
    personId: Int,
    personName: String,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "category_picker/$personId",
        modifier = modifier
    ) {
        composable("category_picker/{personId}") { backStackEntry ->
            val personIdArg = backStackEntry.arguments?.getString("personId")?.toIntOrNull() ?: personId
            
            CategoryPickerScreen(
                personId = personIdArg,
                personName = personName,
                onCategorySelected = { categoryName ->
                    navController.navigate("twenty_questions/$personIdArg/$categoryName")
                },
                onBack = { onComplete() }
            )
        }
        
        composable("twenty_questions/{personId}/{categoryName}") { backStackEntry ->
            val personIdArg = backStackEntry.arguments?.getString("personId")?.toIntOrNull() ?: personId
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            
            CategoryQuestionScreen(
                personId = personIdArg,
                categoryName = categoryName,
                onQuestionsComplete = {
                    navController.navigate("questions_review/$personIdArg") {
                        // Remove the questions screen from back stack so review can't go back to it
                        popUpTo("category_picker/$personIdArg")
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("questions_review/{personId}") { backStackEntry ->
            val personIdArg = backStackEntry.arguments?.getString("personId")?.toIntOrNull() ?: personId
            
            QuestionsReviewScreen(
                personId = personIdArg,
                personName = personName,
                onSave = {
                    onComplete()
                },
                onStartOver = {
                    navController.navigate("category_picker/$personIdArg") {
                        // Clear the entire back stack to start fresh
                        popUpTo("category_picker/$personIdArg") {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuestionsReviewScreen(
    personId: Int,
    personName: String,
    onSave: () -> Unit,
    onStartOver: () -> Unit,
    viewModel: TwentyQuestionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(personId) {
        viewModel.loadPersonAnswers(personId)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Success header
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Great job!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "You've completed the interest questions for $personName",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Summary of collected answers
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Interests Collected",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (uiState.collectedAnswers.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.height(200.dp)
                    ) {
                        items(uiState.collectedAnswers) { answer ->
                            InterestAnswerItem(
                                answer = answer,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                } else {
                    Text(
                        text = "No interests collected yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onStartOver,
                modifier = Modifier.weight(1f)
            ) {
                Text("Start Over")
            }
            
            Button(
                onClick = {
                    viewModel.finishFlow()
                    onSave()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Save Interests")
            }
        }
    }
}

@Composable
private fun InterestAnswerItem(
    answer: InterestAnswer,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = answer.question,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = when (answer.response) {
                true -> "Yes"
                false -> "No"
                null -> "Skip"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = when (answer.response) {
                true -> MaterialTheme.colorScheme.primary
                false -> MaterialTheme.colorScheme.error
                null -> MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontWeight = FontWeight.Medium
        )
    }
}