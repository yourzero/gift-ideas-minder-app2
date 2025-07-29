//// app/src/main/java/com/giftideaminder/ui/navigation/aAppScaffold.kt
//
//package com.giftideaminder.ui.navigation
//
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.navigation.NavGraph.Companion.findStartDestination
//import androidx.navigation.NavHostController
//import androidx.navigation.NavType
//import androidx.navigation.compose.*
//import androidx.navigation.navArgument
//import com.giftideaminder.ui.screens.AddEditGiftScreen
//import com.giftideaminder.ui.screens.EventListScreen
//import com.giftideaminder.ui.screens.GiftListScreen
//import com.giftideaminder.ui.screens.HomeDashboardScreen
//import com.giftideaminder.ui.screens.PersonListScreen
//
//@Composable
//fun AppScaffold(
//    navController: NavHostController,
//    onFabClick: () -> Unit
//) {
//    Scaffold(
//        floatingActionButton = {
//            FloatingActionButton(onClick = onFabClick) {
//                Icon(Icons.Default.Add, contentDescription = "Add Gift")
//            }
//        },
//        bottomBar = {
//            NavigationBar {
//                val currentRoute = navController
//                    .currentBackStackEntryAsState().value?.destination?.route
//                fun navTo(route: String) {
//                    navController.navigate(route) {
//                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
//                        launchSingleTop = true
//                        restoreState = true
//                    }
//                }
//                NavigationBarItem(
//                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
//                    selected = currentRoute == "home",
//                    onClick = { navTo("home") }
//                )
//                NavigationBarItem(
//                    icon = { Icon(Icons.Default.CardGiftcard, contentDescription = "Gifts") },
//                    selected = currentRoute == "giftList",
//                    onClick = { navTo("giftList") }
//                )
//                NavigationBarItem(
//                    icon = { Icon(Icons.Default.Event, contentDescription = "Events") },
//                    selected = currentRoute == "eventList",
//                    onClick = { navTo("eventList") }
//                )
//                NavigationBarItem(
//                    icon = { Icon(Icons.Default.Person, contentDescription = "People") },
//                    selected = currentRoute == "personList",
//                    onClick = { navTo("personList") }
//                )
//            }
//        }
//    ) { innerPadding ->
//        NavHost(
//            navController = navController,
//            startDestination = "home",
//            modifier = Modifier.padding(innerPadding)
//        ) {
//            composable("home")      { HomeDashboardScreen(navController = navController) }
//            composable("giftList")  { GiftListScreen(navController) }
//            composable("eventList") { EventListScreen(navController) }
//            composable("personList"){ PersonListScreen(navController) }
//            composable(
//                "addGift?sharedText={sharedText}",
//                arguments = listOf(navArgument("sharedText") {
//                    type = NavType.StringType; nullable = true; defaultValue = null
//                })
//            ) { back ->
//                AddEditGiftScreen(
//                    navController = navController,
//                    prefillText = back.arguments?.getString("sharedText")
//                )
//            }
//            // … any other routes …
//        }
//    }
//}
