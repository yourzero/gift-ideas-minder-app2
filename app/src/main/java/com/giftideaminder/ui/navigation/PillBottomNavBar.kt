package com.giftideaminder.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.giftideaminder.ui.theme.NavBarBg

@Composable
fun PillBottomNavBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
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

    NavigationBar(
        containerColor = NavBarBg,
        tonalElevation = 0.dp,
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 30.dp)
            .fillMaxWidth()
            .height(80.dp)
            .shadow(8.dp, RoundedCornerShape(40.dp))
            .clip(RoundedCornerShape(40.dp))
    ) {
        BottomNavItem(Icons.Default.Home, "home", "Home", currentRoute, navTo)
        BottomNavItem(Icons.Default.CardGiftcard, "gift_list", "Gifts", currentRoute, navTo)
        BottomNavItem(Icons.Default.Event, "event_list", "Events", currentRoute, navTo)
        BottomNavItem(Icons.Default.Person, "person_list", "People", currentRoute, navTo)
    }
}