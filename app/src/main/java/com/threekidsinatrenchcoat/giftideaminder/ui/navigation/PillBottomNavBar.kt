// app/src/main/java/com/threekidsinatrenchcoat/giftideaminder/ui/navigation/PillBottomNavBar.kt
package com.threekidsinatrenchcoat.giftideaminder.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions

data class NavItem(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit
)

@Composable
fun PillBottomNavBar(
    navController: NavController,
    items: List<NavItem> = defaultItems(),
    modifier: Modifier = Modifier
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val currentRoute = currentDestination?.route.orEmpty()

    var showDiscardDialog by remember { mutableStateOf(false) }
    var pendingRoute by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentRoute) {
        if (showDiscardDialog && pendingRoute == null) {
            showDiscardDialog = false
        }
    }

    val isEditingRoute = remember(currentRoute) {
        currentRoute.startsWith("add_") || currentRoute.startsWith("edit_")
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false; pendingRoute = null },
            title = { Text("Discard changes?") },
            text = { Text("You have an add/edit screen open. If you leave now, any unsaved changes will be lost.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val target = pendingRoute
                        showDiscardDialog = false
                        pendingRoute = null
                        if (!target.isNullOrBlank()) {
                            navController.navigate(
                                target,
                                navOptions {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            )
                        }
                    }
                ) { Text("Discard") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        pendingRoute = null
                    }
                ) { Text("Cancel") }
            }
        )
    }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        modifier = modifier
            .height(64.dp)
            .windowInsetsPadding(WindowInsets(0, 0, 0, 0))
    ) {
        items.forEach { item ->
            val selected = currentDestination.isInHierarchy(item.route)
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (item.route == currentRoute) return@NavigationBarItem
                    if (isEditingRoute) {
                        pendingRoute = item.route
                        showDiscardDialog = true
                    } else {
                        navController.navigate(
                            item.route,
                            navOptions {
                                launchSingleTop = true
                                restoreState = true
                            }
                        )
                    }
                },
                icon = { item.icon() },
                label = { Text(item.label) }
            )
        }
    }
}

@Composable
private fun defaultItems(): List<NavItem> = listOf(
    NavItem(
        route = "home",
        label = "Home",
        icon = { Icon(Icons.Filled.Home, contentDescription = "Home") }
    ),
    NavItem(
        route = "gifts",
        label = "Gifts",
        icon = { Icon(Icons.Filled.ListAlt, contentDescription = "Gifts") }
    ),
    NavItem(
        route = "people",
        label = "People",
        icon = { Icon(Icons.Filled.Person, contentDescription = "People") }
    )
)

private fun NavDestination?.isInHierarchy(route: String): Boolean {
    if (this == null) return false
    return hierarchy.any { it.route == route }
}
