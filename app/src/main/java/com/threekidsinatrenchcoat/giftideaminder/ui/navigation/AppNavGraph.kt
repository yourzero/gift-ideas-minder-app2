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
import androidx.compose.material3.SnackbarHostState
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
            GiftListScreen(navController = navController)
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
            // TODO - add passing the person in
            AddEditRecipientFlowScreen(
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
        composable("add_gift") {
            AddEditGiftScreen(
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
            AddEditGiftScreen(
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
