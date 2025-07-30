package com.giftideaminder.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.giftideaminder.ui.screens.*

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
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
        composable(
            "add_gift?sharedText={sharedText}",
            arguments = listOf(navArgument("sharedText") {
                type = NavType.StringType; nullable = true; defaultValue = null
            })
        ) { back ->
            AddEditGiftScreen(
                navController = navController,
                sharedText = back.arguments?.getString("sharedText")
                //prefillText = back.arguments?.getString("sharedText")
            )
        }
        // … other routes …
    }
}
