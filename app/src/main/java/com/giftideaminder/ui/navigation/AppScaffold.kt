package com.giftideaminder.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.giftideaminder.ui.screens.*
import com.giftideaminder.ui.screens.AddEditGiftScreen

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.giftideaminder.ui.screens.AddEditGiftScreen
import com.giftideaminder.ui.screens.GiftDetailScreen
import com.giftideaminder.ui.screens.GiftListScreen
import com.giftideaminder.ui.screens.PersonListScreen
import com.giftideaminder.ui.screens.AddEditPersonScreen
import com.giftideaminder.ui.screens.ImportScreen
import com.giftideaminder.ui.screens.BudgetScreen
import com.giftideaminder.viewmodel.GiftViewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.giftideaminder.ui.theme.BgPink
import com.giftideaminder.ui.theme.FabPeach
import com.giftideaminder.ui.theme.NavBarBg
import java.util.Calendar

@Composable
fun AppScaffold(
    navController: NavHostController,
    onFabClick: () -> Unit
)
{
    Scaffold(
        containerColor = BgPink,
//        floatingActionButton =
//            {
//                FloatingActionButton(onClick = onFabClick)
//                {
//                    Icon(Icons.Default.Add, contentDescription = "Add Gift")
//                }
//            },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("add_gift") {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                containerColor = FabPeach,
                modifier = Modifier.offset(y = (-60).dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Gift")
            }
        },
        bottomBar =
            {
                NavigationBar(
                    containerColor = NavBarBg,
                    tonalElevation = 4.dp,
                    modifier = Modifier

                        //.align(Alignment.BottomCenter)
                        //.align(LineHeightStyle.Alignment.BottomCenter)

                        //.align(LineHeightStyle.Alignment.BottomCenter)
                        .padding(horizontal = 24.dp, vertical = 30.dp)
                        .height(80.dp)
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(28.dp))
                        .clip(RoundedCornerShape(28.dp))


                )
                {
                    val currentRoute = navController
                        .currentBackStackEntryAsState().value
                        ?.destination?.route

                    fun navTo(route: String)
                    {
                        navController.navigate(route)
                        {
                            popUpTo(navController.graph.findStartDestination().id)
                            {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }

                    NavigationBarItem(
                        //modifier = Modifier.padding(vertical = 18.dp), //.height(60.dp),
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
            navController = navController,
            startDestination = "home",
            modifier = Modifier
                .padding(innerPadding)
                //.height(60.dp)
        )
        {
            fun navTo(route: String)
            {
                navController.navigate(route)
                {
                    popUpTo(navController.graph.findStartDestination().id)
                    {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }

            composable("home")
            {                // HomeDashboardScreen takes (name: String, navController: NavController)
                HomeDashboardScreen(
                    name = "Guest",            // replace with real user name if you like
                    navController = navController
                )
            }

            composable("gift_list")
            {                // GiftListScreen(navController: NavController)
                GiftListScreen(navController = navController)
                //navTo("gift_list")
            }

            composable("event_list")
            {                // EventListScreen(navController: NavController)
                EventListScreen(navController = navController)
            }

            composable("person_list")
            {                // PersonListScreen(navController: NavController)
                PersonListScreen(navController = navController)
            }

            composable(
                route = "add_gift?sharedText={sharedText}",
                arguments = listOf(
                    navArgument("sharedText")
                    {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    })
            ) { backStackEntry ->
                val sharedText = backStackEntry.arguments?.getString("sharedText")
                // AddEditGiftScreen(navController: NavController, prefillText: String? = null)
                AddEditGiftScreen(
                    navController = navController,

                    sharedText = sharedText
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
