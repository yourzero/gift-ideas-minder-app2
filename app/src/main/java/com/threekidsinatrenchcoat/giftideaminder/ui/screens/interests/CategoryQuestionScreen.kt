package com.threekidsinatrenchcoat.giftideaminder.ui.screens.interests

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.TwentyQuestionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryQuestionScreen(
    personId: Int,
    categoryName: String,
    navController: NavController,
    viewModel: TwentyQuestionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var currentAnswer by remember { mutableStateOf("") }
    
    // Initialize category if not already set
    LaunchedEffect(categoryName) {
        if (uiState.currentCategory != categoryName) {
            viewModel.selectCategory(categoryName)
        }
    }
    
    // Update current answer when question changes
    LaunchedEffect(uiState.currentQuestionIndex) {
        currentAnswer = viewModel.getCurrentAnswer()
    }
    
    // Get current question and progress
    val currentQuestion = viewModel.getCurrentQuestion()
    val (currentQuestionNum, totalQuestions) = viewModel.getCategoryProgress()
    val progress = if (totalQuestions > 0) (currentQuestionNum - 1).toFloat() / totalQuestions.toFloat() else 0f
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            CategoryQuestionBottomBar(
                canGoBack = !viewModel.isFirstQuestion(),
                canGoNext = true,
                isAnswered = currentAnswer.isNotBlank(),
                isLastQuestion = viewModel.isLastQuestion(),
                onPrevious = {
                    viewModel.previousQuestion()
                },
                onSkip = {
                    viewModel.answerQuestion("")
                    if (viewModel.isLastQuestion()) {
                        navController.navigateUp()
                    } else {
                        viewModel.nextQuestion()
                    }
                },
                onNext = {
                    viewModel.answerQuestion(currentAnswer)
                    if (viewModel.isLastQuestion()) {
                        navController.navigateUp()
                    } else {
                        viewModel.nextQuestion()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Progress indicator
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
            
            // Question content
            if (currentQuestion != null) {
                AnimatedContent(
                    targetState = uiState.currentQuestionIndex,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally { width -> width } + fadeIn(animationSpec = tween(300))).togetherWith(
                                slideOutHorizontally { width -> -width } + fadeOut(animationSpec = tween(300))
                            )
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn(animationSpec = tween(300))).togetherWith(
                                slideOutHorizontally { width -> width } + fadeOut(animationSpec = tween(300))
                            )
                        }
                    },
                    label = "question_transition"
                ) { questionIndex ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Progress text
                        Text(
                            text = "Question $currentQuestionNum of $totalQuestions",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 32.dp)
                        )
                        
                        // Question text
                        Text(
                            text = currentQuestion,
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 40.dp)
                        )
                        
                        // Answer input field
                        OutlinedTextField(
                            value = currentAnswer,
                            onValueChange = { currentAnswer = it },
                            label = { Text("Your answer") },
                            placeholder = { Text("Share your thoughts...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            keyboardOptions = KeyboardOptions(
                                imeAction = if (currentAnswer.isNotBlank()) ImeAction.Next else ImeAction.Default
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    if (currentAnswer.isNotBlank()) {
                                        viewModel.answerQuestion(currentAnswer)
                                        if (viewModel.isLastQuestion()) {
                                            keyboardController?.hide()
                                            navController.navigateUp()
                                        } else {
                                            viewModel.nextQuestion()
                                        }
                                    }
                                }
                            ),
                            minLines = 3,
                            maxLines = 6,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        
                        // Hint text
                        Text(
                            text = "Tip: Be specific to help us find better gift ideas!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        )
                    }
                }
            }
            
            // Loading state
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            // Error state
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
    
    // Auto-focus the text field when screen loads
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun CategoryQuestionBottomBar(
    canGoBack: Boolean,
    canGoNext: Boolean,
    isAnswered: Boolean,
    isLastQuestion: Boolean,
    onPrevious: () -> Unit,
    onSkip: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous button
            TextButton(
                onClick = onPrevious,
                enabled = canGoBack,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Previous",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Skip button
            OutlinedButton(
                onClick = onSkip,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (isLastQuestion) "Finish" else "Skip",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Next button
            Button(
                onClick = onNext,
                enabled = canGoNext,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAnswered) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                Text(
                    text = if (isLastQuestion) "Complete" else "Next",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isAnswered) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}