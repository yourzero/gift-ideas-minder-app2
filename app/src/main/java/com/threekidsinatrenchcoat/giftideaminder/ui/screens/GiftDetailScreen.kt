package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.threekidsinatrenchcoat.giftideaminder.data.model.PriceRecord
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.GiftViewModel
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.PersonViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.threekidsinatrenchcoat.giftideaminder.ui.components.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiftDetailScreen(
    giftId: Int,
    viewModel: GiftViewModel = hiltViewModel(),
    navController: NavController
) {
    // Collect the gift; don't early-return inside a lambda (causes the compiler error).
    val giftWithHistory = viewModel
        .getGiftWithHistoryById(giftId)
        .collectAsState(initial = null)
        .value

    Scaffold(
        topBar = { AppTopBar("Gift Details") }
    ) { innerPadding ->
        if (giftWithHistory == null) {
            // Loading/empty state instead of 'return' inside the Scaffold content lambda
            Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
                Text("Loading…")
            }
        } else {
            val gift = giftWithHistory.gift
            val history: List<PriceRecord> = giftWithHistory.currentPriceHistory

            val personViewModel: PersonViewModel = hiltViewModel()
            val persons = personViewModel.allPersons.collectAsState(initial = emptyList()).value
            val personName = persons.find { it.id == gift.personId }?.name ?: "None"

            Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
                Text("Title: ${gift.title}")
                Text("Description: ${gift.description ?: "No description"}")
                Text("URL: ${gift.url ?: "No URL"}")
                Text("Saved Price: $${gift.originalPrice ?: 0.0}")
                Text("Current Price: ${gift.currentPrice?.let { "$$it" } ?: "Not fetched"}")
                Text(
                    "Event Date: " + (
                            gift.eventDate?.let {
                                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    .format(Date(it))
                            } ?: "Not set"
                            )
                )
                Text("Assigned to: $personName")

                TextField(
                    value = gift.budget?.toString() ?: "",
                    onValueChange = { newBudget ->
                        viewModel.updateGift(gift.copy(budget = newBudget.toDoubleOrNull()))
                    },
                    label = { Text("Budget") }
                )

                RowWithCheckbox(
                    checked = gift.isPurchased,
                    onCheckedChange = { isPurchased ->
                        viewModel.updateGift(gift.copy(isPurchased = isPurchased))
                    },
                    label = "Purchased"
                )

                Button(onClick = { viewModel.updatePriceForGift(gift) }) {
                    Text("Update Price")
                }

                if (history.isNotEmpty()) {
                    Text("Price History:")
                    LazyColumn {
                        items(items = history) { record: PriceRecord ->
                            val date = SimpleDateFormat(
                                "MM/dd/yyyy",
                                Locale.getDefault()
                            ).format(Date(record.timestamp))
                            Text("$date: $${record.price}")
                        }
                    }
                }

                Button(onClick = { navController.navigate("edit_gift/$giftId") }) {
                    Text("Edit")
                }
                Button(onClick = {
                    viewModel.deleteGift(gift)
                    navController.popBackStack()
                }) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
private fun RowWithCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String
) {
    Column {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(label)
    }
}