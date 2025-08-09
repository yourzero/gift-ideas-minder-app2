package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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


@Preview
@Composable
fun BudgetScreen(navController: NavController) {
    val viewModel: GiftViewModel = hiltViewModel()
    val gifts by viewModel.allGifts.collectAsState(initial = emptyList<Gift>())

    val totalBudget: Double = gifts.sumOf { it.budget ?: 0.0 }
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