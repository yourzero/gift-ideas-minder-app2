//package com.threekidsinatrenchcoat.giftideaminder.ui.navigation
//
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.navigation.NavHostController
//import androidx.navigation.NavType
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import androidx.navigation.navArgument
//import com.threekidsinatrenchcoat.giftideaminder.ui.screens.AddEditGiftScreen
//import com.threekidsinatrenchcoat.giftideaminder.ui.screens.GiftDetailScreen
//import com.threekidsinatrenchcoat.giftideaminder.ui.screens.GiftListScreen
//import com.threekidsinatrenchcoat.giftideaminder.ui.screens.PersonListScreen
//import com.threekidsinatrenchcoat.giftideaminder.ui.screens.AddEditPersonScreen
//import com.threekidsinatrenchcoat.giftideaminder.ui.screens.ImportScreen
//import com.threekidsinatrenchcoat.giftideaminder.ui.screens.BudgetScreen
//import com.threekidsinatrenchcoat.giftideaminder.viewmodel.GiftViewModel
//import androidx.compose.ui.tooling.preview.Preview
//
//
//@Preview
//@Composable
//fun Navigation(viewModel: GiftViewModel, sharedText: String? = null) {
//    val navController = rememberNavController()
//
//    if (sharedText != null) {
//        LaunchedEffect(Unit) {
//            navController.navigate("add_gift_with_share/$sharedText")
//        }
//    }
//
//    NavHost(navController = navController, startDestination = "gift_list") {
//        composable("gift_list") {
//            GiftListScreen(viewModel = viewModel, navController = navController)
//        }
//        composable("add_gift") {
//            AddEditGiftScreen(viewModel = viewModel, navController = navController, giftId = null)
//        }
//        composable("gift_detail/{giftId}") { backStackEntry ->
//            val giftId = backStackEntry.arguments?.getString("giftId")?.toIntOrNull() ?: 0
//            GiftDetailScreen(giftId = giftId, viewModel = viewModel, navController = navController)
//        }
//        composable("edit_gift/{giftId}") { backStackEntry ->
//            val giftId = backStackEntry.arguments?.getString("giftId")?.toIntOrNull() ?: 0
//            AddEditGiftScreen(viewModel = viewModel, navController = navController, giftId = giftId)
//        }
//        composable("person_list") {
//            PersonListScreen(navController = navController)
//        }
//        composable("add_person") {
//            AddEditPersonScreen(navController = navController, personId = null)
//        }
//        composable("edit_person/{personId}") { backStackEntry ->
//            val personId = backStackEntry.arguments?.getString("personId")?.toIntOrNull() ?: 0
//            AddEditPersonScreen(navController = navController, personId = personId)
//        }
//        composable("import") {
//            ImportScreen(navController = navController)
//        }
//        composable("budget") {
//            BudgetScreen(navController = navController)
//        }
//        composable(
//            "add_gift_with_share/{sharedText}",
//            arguments = listOf(navArgument("sharedText") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val text = backStackEntry.arguments?.getString("sharedText") ?: ""
//            AddEditGiftScreen(viewModel = viewModel, navController = navController, giftId = null, sharedText = text)
//        }
//    }
//}