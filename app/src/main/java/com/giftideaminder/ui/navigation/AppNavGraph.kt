package com.giftideaminder.ui.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.giftideaminder.ui.screens.*
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
)
{
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
            GiftListScreen(navController = navController)
        }
        composable("event_list") {
            EventListScreen(navController)
        }
        composable("person_list") {
            PersonListScreen(navController)
        }
        composable("add_person") {
            AddEditRecipientFlowScreen(onNavigateBack = ::showSnackbarAndPopBackStack)
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
        // TODO - add passing the person in
        AddEditRecipientFlowScreen(
            personId = personId,
            onNavigateBack = ::showSnackbarAndPopBackStack
        )
    }
    composable(
        "add_gift?sharedText={sharedText}",
        arguments = listOf(navArgument("sharedText") {
            type = NavType.StringType; nullable = true; defaultValue = null
        })
    ) { back ->
        AddEditGiftScreen(
            onNavigateBack = { /* Gift handles back via navController + snackbar */ },
            navController = navController,
            sharedText = back.arguments?.getString("sharedText")
        )
    }
    // … other routes …
}
}
