package com.giftideaminder.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.giftideaminder.ui.screens.AddEditGiftScreen
import com.giftideaminder.ui.screens.GiftDetailScreen
import com.giftideaminder.ui.screens.GiftListScreen
import com.giftideaminder.viewmodel.GiftViewModel

@Composable
fun Navigation(viewModel: GiftViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "gift_list") {
        composable("gift_list") {
            GiftListScreen(viewModel = viewModel, navController = navController)
        }
        composable("add_gift") {
            AddEditGiftScreen(viewModel = viewModel, navController = navController, giftId = null)
        }
        composable("gift_detail/{giftId}") { backStackEntry ->
            val giftId = backStackEntry.arguments?.getString("giftId")?.toIntOrNull() ?: 0
            GiftDetailScreen(giftId = giftId, viewModel = viewModel, navController = navController)
        }
        composable("edit_gift/{giftId}") { backStackEntry ->
            val giftId = backStackEntry.arguments?.getString("giftId")?.toIntOrNull() ?: 0
            AddEditGiftScreen(viewModel = viewModel, navController = navController, giftId = giftId)
        }
    }
} 