package com.giftideaminder.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.giftideaminder.ui.screens.*
import com.giftideaminder.ui.screens.AddEditGiftScreen

@Composable
fun AppScaffold(
    navController: NavHostController,
    onFabClick: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onFabClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Gift")
            }
        },
        bottomBar = {
            NavigationBar {
                val currentRoute = navController
                    .currentBackStackEntryAsState().value
                    ?.destination?.route

                fun navTo(route: String) {
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    selected = currentRoute == "home",
                    onClick = { navTo("home") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.CardGiftcard, contentDescription = "Gifts") },
                    selected = currentRoute == "gift_list",
                    onClick = { navTo("gift_list") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Event, contentDescription = "Events") },
                    selected = currentRoute == "event_list",
                    onClick = { navTo("event_list") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "People") },
                    selected = currentRoute == "person_list",
                    onClick = { navTo("person_list") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController  = navController,
            startDestination = "home",
            modifier       = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                // HomeDashboardScreen takes (name: String, navController: NavController)
                HomeDashboardScreen(
                    name          = "Guest",            // replace with real user name if you like
                    navController = navController
                )
            }

            composable("gift_list") {
                // GiftListScreen(navController: NavController)
                GiftListScreen(navController = navController)
            }

            composable("event_list") {
                // EventListScreen(navController: NavController)
                EventListScreen(navController = navController)
            }

            composable("person_list") {
                // PersonListScreen(navController: NavController)
                PersonListScreen(navController = navController)
            }

            composable(
                route = "add_gift?sharedText={sharedText}",
                arguments = listOf(navArgument("sharedText") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val sharedText = backStackEntry.arguments?.getString("sharedText")
                // AddEditGiftScreen(navController: NavController, prefillText: String? = null)
                AddEditGiftScreen(
                    navController = navController,

                    sharedText   = sharedText
                )
                /*
                viewModel: GiftViewModel,
    navController: NavController,
    giftId: Int?,
    sharedText: String? = null
                 */
            }
        }
    }
}
