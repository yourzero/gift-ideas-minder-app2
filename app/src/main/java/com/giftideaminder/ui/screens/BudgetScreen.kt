package com.giftideaminder.ui.screens

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
import com.giftideaminder.viewmodel.GiftViewModel
import kotlin.math.abs
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.giftideaminder.data.model.Gift
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import com.giftideaminder.ui.components.SuggestionsCarousel


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

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        var min by remember { mutableStateOf("25") }
        var max by remember { mutableStateOf("100") }
        OutlinedTextField(value = min, onValueChange = { min = it }, label = { Text("Min budget ($)") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = max, onValueChange = { max = it }, label = { Text("Max budget ($)") })
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            val minV = min.toDoubleOrNull() ?: 0.0
            val maxV = max.toDoubleOrNull() ?: minV
            viewModel.fetchSuggestionsByBudget(minV, maxV, personId = null)
        }) { Text("Find AI ideas") }

        Spacer(Modifier.height(16.dp))
        SuggestionsCarousel(
            suggestions = viewModel.suggestions,
            onAccept = { suggestion -> viewModel.insertGift(suggestion.copy(id = 0)) },
            onDismiss = { suggestion -> viewModel.dismissSuggestion(suggestion) },
            isLoading = viewModel.isLoadingSuggestions,
            error = viewModel.suggestionsError
        )
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