package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.GiftViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.threekidsinatrenchcoat.giftideaminder.data.model.Gift
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment



@Preview
@Composable
fun BudgetScreen(navController: NavController) {
Scaffold(
    topBar = {
CenterAlignedTopAppBar(
    modifier = Modifier.height(48.dp),
    title = {
        Box(
            modifier = Modifier.height(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Gift Budget", style = MaterialTheme.typography.titleMedium)
        }
    },
    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
)
    }
) { innerPadding ->
    Column(modifier = Modifier.padding(innerPadding)) {
val viewModel: GiftViewModel = hiltViewModel()
    val gifts by viewModel.allGifts.collectAsState(initial = emptyList<Gift>())

    val totalBudget: Double = gifts.sumOf { it.budget ?: 0.0 }
        CenterAlignedTopAppBar(
            title = { Text("Gift Budget", style = MaterialTheme.typography.titleLarge) }
        )
    val totalSpent: Double = gifts.filter { it.isPurchased }.sumOf { it.purchasePrice ?: 0.0 }
    val remaining: Double = totalBudget - totalSpent

    var showAlert by remember { mutableStateOf(remaining < 50.0 && remaining > 0.0) }  // Alert if within $50

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Total Budget: $$totalBudget")
        Text("Total Spent: $$totalSpent")
        Text("Remaining: $$remaining")
    }

    if (showAlert) {
        AlertDialog(
            onDismissRequest = { showAlert = false },
            title = { Text("Budget Alert") },
            text = { Text("You're approaching your budget limit! Remaining: $$remaining") },
            confirmButton = {
                Button(onClick = { showAlert = false }) {
                    Text("OK")
                }
            }
        )
    }
    }
}
} 