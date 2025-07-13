package com.giftideaminder.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.giftideaminder.viewmodel.GiftViewModel
import com.giftideaminder.viewmodel.PersonViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.TextField
import androidx.compose.material3.Checkbox
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@Composable
fun GiftDetailScreen(giftId: Int, viewModel: GiftViewModel, navController: NavController) {
    val personViewModel: PersonViewModel = hiltViewModel()
    val gift = viewModel.getGiftById(giftId).collectAsState(initial = null).value ?: return
    val persons = personViewModel.allPersons.collectAsState(initial = emptyList()).value
    val personName = persons.find { it.id == gift.personId }?.name ?: "None"

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Title: ${gift.title}")
        Text("Description: ${gift.description ?: "No description"}")
        Text("URL: ${gift.url ?: "No URL"}")
        Text("Price: $${gift.price ?: 0.0}")
        Text("Event Date: ${gift.eventDate?.let { SimpleDateFormat("dd/MM/yyyy").format(Date(it)) } ?: "Not set"}")
        Text("Assigned to: $personName")
        Text("Saved Price: $${gift.price ?: 0.0}")
        Text("Current Price: $${gift.currentPrice ?: "Not fetched"}")
        if (gift.currentPrice != null && gift.price != null && gift.currentPrice < gift.price) {
            Text("Deal Alert! Saved $${gift.price - gift.currentPrice}", color = Color.Green)
        }
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
        if (gift.priceHistory != null) {
            Text("Price History:")
            LazyColumn {
                items(gift.priceHistory) { (date, price) ->
                    Text("$date: $$price")
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