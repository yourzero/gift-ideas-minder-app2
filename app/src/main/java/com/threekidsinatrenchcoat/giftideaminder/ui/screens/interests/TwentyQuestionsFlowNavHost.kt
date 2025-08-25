package com.threekidsinatrenchcoat.giftideaminder.ui.screens.interests

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.TwentyQuestionsViewModel

@Composable
fun TwentyQuestionsFlowNavHost(
    personId: Long,
    onComplete: () -> Unit,
    onCancel: () -> Unit,
    navController: NavHostController = rememberNavController(),
    viewModel: TwentyQuestionsViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = "category_picker"
    ) {
        composable("category_picker") {
            CategoryPickerScreen(
                personId = personId,
                onNavigateToQuestions = {
                    navController.navigate("questions") {
                        popUpTo("category_picker") { inclusive = false }
                    }
                },
                onBack = {
                    viewModel.resetFlow()
                    onCancel()
                },
                viewModel = viewModel
            )
        }
        
        composable("questions") {
            CategoryQuestionScreen(
                onNavigateToReview = {
                    navController.navigate("review") {
                        popUpTo("questions") { inclusive = false }
                    }
                },
                onBack = {
                    viewModel.resetFlow()
                    onCancel()
                },
                viewModel = viewModel
            )
        }
        
        composable("review") {
            TwentyQuestionsReviewScreen(
                onComplete = onComplete,
                onStartOver = {
                    viewModel.resetFlow()
                    navController.navigate("category_picker") {
                        popUpTo("review") { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }
    }
}