package com.giftideaminder.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch

/**
 * Example of how to handle the success message from AddEditGifteeScreen
 * in a parent screen like PersonListScreen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonListScreenExample() {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    
    // This would be your navigation state/logic
    var showAddEditScreen by remember { mutableStateOf(false) }
    
    // Handle success message from AddEditGifteeScreen
    LaunchedEffect(Unit) {
        // This is where you'd collect the message when navigating back
        // In a real app, this would come from your navigation system
    }
    
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddEditScreen = true }
            ) {
                Text("+")
            }
        }
    ) { padding ->
        // Your person list content here
        
        if (showAddEditScreen) {
            AddEditRecipientScreen(
                onNavigateBack = { successMessage ->
                    showAddEditScreen = false
                    
                    // Show snackbar if there's a success message
                    successMessage?.let { message ->
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = message,
                                duration = SnackbarDuration.Long
                            )
                        }
                    }
                }
            )
        }
    }
}

/**
 * Example for Navigation Compose setup
 */
/*
@Composable
fun YourNavHost() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "person_list",
            modifier = Modifier.padding(padding)
        ) {
            composable("person_list") {
                PersonListScreen(
                    onNavigateToAddEdit = { personId ->
                        navController.navigate("add_edit_giftee/${personId ?: "new"}")
                    }
                )
            }
            
            composable(
                "add_edit_giftee/{personId}",
                arguments = listOf(navArgument("personId") { 
                    type = NavType.StringType 
                    nullable = true 
                })
            ) { backStackEntry ->
                val personIdString = backStackEntry.arguments?.getString("personId")
                val personId = if (personIdString == "new") null else personIdString?.toIntOrNull()
                
                AddEditGifteeScreen(
                    personId = personId,
                    onNavigateBack = { successMessage ->
                        navController.popBackStack()
                        
                        // Show snackbar on the parent screen
                        successMessage?.let { message ->
                            // Use a coroutine scope to show the snackbar
                            // You'd inject this scope or use rememberCoroutineScope() in the parent
                            kotlinx.coroutines.GlobalScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = message,
                                    duration = SnackbarDuration.Long
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}
*/