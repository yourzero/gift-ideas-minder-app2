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
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun GiftDetailScreen(giftId: Int, viewModel: GiftViewModel, navController: NavController) {
    val gift = viewModel.getGiftById(giftId).collectAsState(initial = null).value ?: return
    val persons = viewModel.allPersons.collectAsState(initial = emptyList()).value
    val personName = persons.find { it.id == gift.personId }?.name ?: "None"

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Title: ${gift.title}")
        Text("Description: ${gift.description ?: "No description"}")
        Text("URL: ${gift.url ?: "No URL"}")
        Text("Price: $${gift.price ?: 0.0}")
        Text("Event Date: ${gift.eventDate?.let { SimpleDateFormat("dd/MM/yyyy").format(Date(it)) } ?: "Not set"}")
        Text("Assigned to: $personName")

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