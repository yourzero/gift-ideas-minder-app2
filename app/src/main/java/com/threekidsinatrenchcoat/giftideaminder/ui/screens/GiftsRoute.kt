package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.threekidsinatrenchcoat.giftideaminder.ui.state.*
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.GiftViewModel

/**
 * Temporary integration layer that doesn't assume concrete fields
 * on your Gift model. It renders an empty state (or sample data),
 * keeping compilation green while you finalize DAO/VM shapes.
 */
@Composable
fun GiftsRoute(
    navController: NavController,
    viewModel: GiftViewModel = hiltViewModel()
) {
    val state = remember {
        GiftsUiState(
            groups = listOf(
                GiftGroup(
                    header = "All Gifts",
                    items = emptyList()
                )
            )
        )
    }

    GiftsScreen(
        state = state,
        onUpdateFilters = { filters -> println("Updated filters: $filters") },
        onResetFilters = { println("Reset filters") },
        onSelectTab = { tab -> println("Selected tab: $tab") },
        onSelectGrouping = { grouping -> println("Selected grouping: $grouping") },
        onAddGift = { navController.navigate("add_gift") },
        onSuggest = { /* TODO AI suggest */ },
        onOpenGift = { id -> navController.navigate("gift_detail/$id") },
        onOpenLink = { /* TODO open URL */ },
        onToggleShortlist = { /* TODO */ },
        onMarkPurchased = { /* TODO */ },
        onAssistAction = { /* TODO */ }
    )
}
