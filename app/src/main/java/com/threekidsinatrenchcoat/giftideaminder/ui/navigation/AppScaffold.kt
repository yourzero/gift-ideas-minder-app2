package com.threekidsinatrenchcoat.giftideaminder.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.threekidsinatrenchcoat.giftideaminder.ui.theme.BgPink
import com.threekidsinatrenchcoat.giftideaminder.ui.theme.FabPeach

@Composable
fun AppScaffold(
    navController: NavHostController,
    onFabClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    // Track current route
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    
    // Define route behavior
    val isOnHomeScreen = currentRoute == "home"
    val isOnAddEditScreen = currentRoute?.let { route ->
        route.startsWith("add_") || route.startsWith("edit_") || route.contains("add_gift")
    } ?: false
    val shouldShowFab = !isOnAddEditScreen
    
    // State for submenu visibility
    var isSubmenuVisible by remember { mutableStateOf(false) }
    
    // Reset submenu when route changes
    LaunchedEffect(currentRoute) {
        isSubmenuVisible = false
    }
    
    // Define FAB action based on current screen
    val fabAction = when (currentRoute) {
        "home" -> { 
            { isSubmenuVisible = !isSubmenuVisible }
        }
        "gift_list" -> { 
            { navController.navigate("add_gift") }
        }
        "person_list" -> { 
            { navController.navigate("add_person") }
        }
        "event_list" -> { 
            { navController.navigate("add_gift") } // For now, events add gifts
        }
        else -> onFabClick
    }

    Box {
        Scaffold(
            containerColor = BgPink,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            floatingActionButton = {
                if (shouldShowFab) {
                    FloatingActionButton(
                        onClick = fabAction,
                        containerColor = FabPeach,
                        modifier = Modifier.padding(bottom = 0.dp)
                    ) {
                        Icon(
                            imageVector = if (isOnHomeScreen && isSubmenuVisible) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = when (currentRoute) {
                                "home" -> if (isSubmenuVisible) "Close Menu" else "Add Options"
                                "gift_list" -> "Add Gift"
                                "person_list" -> "Add Person"
                                "event_list" -> "Add Event"
                                else -> "Add"
                            }
                        )
                    }
                }
            },
            bottomBar = {
                PillBottomNavBar(navController)
            }
        ) { innerPadding ->
            AppNavGraph(
                navController = navController,
                snackbarHostState = snackbarHostState,
                modifier = Modifier.padding(innerPadding)
            )
        }
        
        // Submenu overlay
        if (isOnHomeScreen && isSubmenuVisible) {
            // Invisible overlay to dismiss submenu when clicking outside
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { isSubmenuVisible = false }
            )
            
            AddSubmenu(
                navController = navController,
                onDismiss = { isSubmenuVisible = false },
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
private fun AddSubmenu(
    navController: NavHostController,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SubmenuItem(
            icon = Icons.Default.Person,
            text = "Add Person",
            onClick = {
                navController.navigate("add_person")
                onDismiss()
            }
        )
        SubmenuItem(
            icon = Icons.Default.CardGiftcard,
            text = "Add Gift",
            onClick = {
                navController.navigate("add_gift")
                onDismiss()
            }
        )
        SubmenuItem(
            icon = Icons.Default.Event,
            text = "Add Event",
            onClick = {
                navController.navigate("event_list")
                onDismiss()
            }
        )
    }
}

@Composable
private fun SubmenuItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp)),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = FabPeach,
                modifier = Modifier.size(40.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = text,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Black
                    )
                }
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }
    }
}