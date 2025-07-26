package com.threekidsinatrenchcoat.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.threekidsinatrenchcoat.ui.screens.AddEditGiftScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        // ... other composable routes ...

        composable(
            route = "addGift?sharedText={sharedText}",
            arguments = listOf(navArgument("sharedText") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val sharedText = backStackEntry.arguments?.getString("sharedText")
            AddEditGiftScreen(navController = navController, prefillText = sharedText)
        }
    }
}
