package com.threekidsinatrenchcoat.giftideaminder.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.threekidsinatrenchcoat.giftideaminder.ui.theme.NavBarBg

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
        listOf(
            Triple(Icons.Default.Home, "home", "Home"),
            Triple(Icons.Default.CardGiftcard, "gift_list", "Gifts"),
            Triple(Icons.Default.Event, "event_list", "Events"),
            Triple(Icons.Default.Person, "person_list", "People")
        ).forEach { (icon, route, label) ->
            val selected = currentRoute == route
            NavigationBarItem(
                selected = selected,
                onClick = { navTo(route) },
                icon = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(if (selected) Color.White else Color.Transparent)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (selected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = Color.Unspecified,
                    unselectedIconColor = Color.Unspecified
                )
            )
        }
    }
}