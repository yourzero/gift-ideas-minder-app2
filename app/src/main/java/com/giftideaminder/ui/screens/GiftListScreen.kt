package com.giftideaminder.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.giftideaminder.ui.components.GiftItem
import com.giftideaminder.viewmodel.GiftViewModel

@Composable
fun GiftListScreen(viewModel: GiftViewModel, navController: NavController) {
    val gifts = viewModel.allGifts.collectAsState(initial = emptyList()).value

    LazyColumn {
        item {
            Button(onClick = { navController.navigate("add_gift") }) {
                Text("Add New Gift")
            }
            Button(onClick = { navController.navigate("person_list") }) {
                Text("Manage Persons")
            }
        }
        items(gifts) { gift ->
            GiftItem(gift = gift) {
                navController.navigate("gift_detail/${gift.id}")
            }
        }
    }
} 