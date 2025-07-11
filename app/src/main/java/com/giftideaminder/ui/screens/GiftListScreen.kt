package com.giftideaminder.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.giftideaminder.ui.components.GiftItem
import com.giftideaminder.viewmodel.GiftViewModel
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun GiftListScreen(
    viewModel: GiftViewModel,
    navController: NavController
) {
    val giftsState = viewModel.allGifts.collectAsState(initial = emptyList())
    val gifts = giftsState.value

    val sortedGifts = gifts.sortedBy { gift -> gift.eventDate }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Button(onClick = { navController.navigate("add_gift") }) {
                Text("Add New Gift")
            }
        }
        items(
            items = sortedGifts,
            key = { gift -> gift.id }
        ) { gift ->
            GiftItem(gift = gift) {
                navController.navigate("gift_detail/${gift.id}")
            }
        }
    }
} 