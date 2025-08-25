package com.threekidsinatrenchcoat.giftideaminder.ui.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.threekidsinatrenchcoat.giftideaminder.ui.screens.*
import com.threekidsinatrenchcoat.giftideaminder.ui.screens.achievements.AchievementsScreen
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.GiftViewModel
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.PersonViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val snackbarScope = rememberCoroutineScope()

    fun showSnackbarAndPopBackStack(successMessage: String?) {
        successMessage?.let { message ->
            snackbarScope.launch {
                snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
            }
        }
        navController.popBackStack()
    }

    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            HomeDashboardScreen(name = "Guest", navController = navController)
        }
        composable("gift_list") {
            GiftsRoute(navController = navController)
        }
        composable("event_list") {
            EventListScreen(navController)
        }
        composable("person_list") {
            PersonListScreen(navController)
        }
        composable("budget") {
            BudgetScreen(navController = navController)
        }
        composable("settings") {
            SettingsScreen(navController = navController)
        }
        composable("achievements") {
            AchievementsScreen(modifier = modifier)
        }
        composable("import") {
            ImportScreen(navController = navController)
        }
        composable("add_person") {
            AddEditRecipientFlowScreen(onNavigateBack = ::showSnackbarAndPopBackStack, navController = navController)
        }
        composable(
            route = "edit_person/{personId}",
            arguments = listOf(
                navArgument("personId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            var personId = backStackEntry.arguments!!.getInt("personId")
            // Use new tab-based edit screen for editing existing recipients
            EditRecipientTabsScreen(
                personId = personId,
                onNavigateBack = ::showSnackbarAndPopBackStack,
                navController = navController
            )
        }
        composable(
            route = "person_ideas/{personId}",
            arguments = listOf(
                navArgument("personId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val personId = backStackEntry.arguments!!.getInt("personId")
            PersonIdeasScreen(personId = personId, navController = navController)
        }
        composable(
            route = "person_detail/{personId}",
            arguments = listOf(
                navArgument("personId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val personId = backStackEntry.arguments!!.getInt("personId")
            PersonDetailScreen(personId = personId, navController = navController)
        }
        composable("add_gift") {
            AddGiftFlowScreenWrapper(
                onNavigateBack = ::showSnackbarAndPopBackStack,
                navController = navController
            )
        }
        composable(
            "add_gift?sharedText={sharedText}",
            arguments = listOf(navArgument("sharedText") {
                type = NavType.StringType; nullable = true; defaultValue = null
            })
        ) { back ->
            AddGiftFlowScreenWrapper(
                onNavigateBack = ::showSnackbarAndPopBackStack,
                navController = navController,
                sharedText = back.arguments?.getString("sharedText")
            )
        }
        composable(
            route = "edit_gift/{giftId}",
            arguments = listOf(
                navArgument("giftId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val giftId = backStackEntry.arguments!!.getInt("giftId")
            AddEditGiftScreen(
                onNavigateBack = ::showSnackbarAndPopBackStack,
                navController = navController,
                giftId = giftId
            )
        }
        composable(
            route = "gift_detail/{giftId}",
            arguments = listOf(
                navArgument("giftId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val giftId = backStackEntry.arguments!!.getInt("giftId")
            GiftDetailScreen(
                giftId = giftId,
                navController = navController
            )
        }
        // … other routes …
    }
}

@Composable
private fun AddGiftFlowScreenWrapper(
    onNavigateBack: (String?) -> Unit,
    navController: NavController,
    sharedText: String? = null,
    viewModel: GiftViewModel = hiltViewModel(),
    personViewModel: PersonViewModel = hiltViewModel()
) {
    val ui by viewModel.uiState.collectAsState()
    val persons by personViewModel.allPersons.collectAsState(initial = emptyList())
    var showPersonDialog by remember { mutableStateOf(false) }
    
    // Handle shared text initialization
    LaunchedEffect(sharedText) {
        if (!sharedText.isNullOrBlank()) {
            if (sharedText.startsWith("http")) {
                viewModel.onUrlChanged(sharedText)
            } else {
                viewModel.onIdeaTextChanged(sharedText)
            }
        }
    }
    
    // Listen for save events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is GiftViewModel.GiftEvent.Saved -> {
                    onNavigateBack("Gift saved successfully!")
                }
            }
        }
    }
    
    AddGiftFlowScreen(
        selectedPersonName = ui.selectedPersonName,
        selectedPersonId = ui.personId,
        eventDateMillis = if (ui.eventDateMillis > 0) ui.eventDateMillis else null,
        ideaText = ui.ideaText,
        stepIndex = ui.stepIndex,
        occasion = ui.occasion,
        personImportantDates = ui.personImportantDates,
        onSelectPersonClick = {
            showPersonDialog = true
        },
        onDateSelected = { millis ->
            viewModel.onDateSelectedFlow(millis)
        },
        onIdeaChange = { text ->
            viewModel.onIdeaTextChanged(text)
        },
        onOccasionChange = { occasion ->
            viewModel.onOccasionChanged(occasion)
        },
        onBack = {
            if (ui.stepIndex > 0) {
                viewModel.onStepBack()
            } else {
                navController.popBackStack()
            }
        },
        onNext = {
            viewModel.onStepNext()
        },
        onSave = {
            // Populate the title if not set
            if (ui.title.isBlank() && ui.ideaText.isNotBlank()) {
                viewModel.onTitleChanged(ui.ideaText)
            }
            viewModel.onSave()
        },
        onResetForCreate = {
            viewModel.resetForCreateFlow()
        },
        onAddCustomDate = { label, date ->
            viewModel.onAddCustomDate(label, date)
        },
        openedFromFab = sharedText == null // Assume opened from FAB if no shared text
    )
    
    // Person selection dialog
    if (showPersonDialog) {
        AlertDialog(
            onDismissRequest = { showPersonDialog = false },
            title = { Text("Select Person") },
            text = {
                LazyColumn {
                    items(persons.size) { index ->
                        val person = persons[index]
                        TextButton(
                            onClick = {
                                viewModel.onPersonSelectedFlow(person.id, person.name)
                                showPersonDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(person.name, modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPersonDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
