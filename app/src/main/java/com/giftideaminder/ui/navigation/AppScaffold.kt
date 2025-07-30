package com.giftideaminder.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.giftideaminder.ui.theme.BgPink
import com.giftideaminder.ui.theme.FabPeach

@Composable
fun AppScaffold(
    navController: NavHostController,
    onFabClick: () -> Unit
) {
    Scaffold(
        containerColor = BgPink,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onFabClick,
                containerColor = FabPeach,
                modifier = Modifier.padding(bottom = 64.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Gift")
            }
        },
        bottomBar = {
            PillBottomNavBar(navController)
        }
    ) { innerPadding ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}