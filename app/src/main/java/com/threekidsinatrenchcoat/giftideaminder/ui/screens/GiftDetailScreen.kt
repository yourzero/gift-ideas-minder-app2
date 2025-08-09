package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.threekidsinatrenchcoat.giftideaminder.data.model.PriceRecord
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.GiftViewModel
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.PersonViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GiftDetailScreen(
    giftId: Int,
    viewModel: GiftViewModel = hiltViewModel(),
    navController: NavController
) {
    val giftWithHistory = viewModel.getGiftWithHistoryById(giftId)
        .collectAsState(initial = null).value ?: return
    val gift = giftWithHistory.gift
    val history: List<PriceRecord> = giftWithHistory.currentPriceHistory

    val personViewModel: PersonViewModel = hiltViewModel()
    val persons = personViewModel.allPersons.collectAsState(initial = emptyList()).value
    val personName = persons.find { it.id == gift.personId }?.name ?: "None"

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Title: ${gift.title}")
        Text("Description: ${gift.description ?: "No description"}")
        Text("URL: ${gift.url ?: "No URL"}")
        Text("Saved Price: $${gift.originalPrice ?: 0.0}")
        Text("Current Price: $${gift.currentPrice ?: "Not fetched"}")
        Text("Event Date: ${gift.eventDate?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it)) } ?: "Not set"}")
        Text("Assigned to: $personName")

        TextField(
            value = gift.budget?.toString() ?: "",
            onValueChange = { newBudget ->
                viewModel.updateGift(gift.copy(budget = newBudget.toDoubleOrNull()))
            },
            label = { Text("Budget") }
        )

        Checkbox(
            checked = gift.isPurchased,
            onCheckedChange = { isPurchased ->
                viewModel.updateGift(gift.copy(isPurchased = isPurchased))
            }
        )
        Text("Purchased")

        Button(onClick = { viewModel.updatePriceForGift(gift) }) {
            Text("Update Price")
        }

        if (history.isNotEmpty()) {
            Text("Price History:")
            LazyColumn {
                items(items = history) { record: PriceRecord ->
                    val date = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date(record.timestamp))
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
