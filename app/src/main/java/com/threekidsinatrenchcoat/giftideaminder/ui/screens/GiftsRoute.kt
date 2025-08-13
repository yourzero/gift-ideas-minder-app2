package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.threekidsinatrenchcoat.giftideaminder.ui.state.*
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.GiftViewModel

/**
 * Gifts screen with person-specific structure:
 * - Upcoming Gifts: Shows gifts with upcoming events
 * - Gifts by Person: Shows gifts grouped by person (with suggestions)
 * Suggestions are person-specific, not general.
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
                    header = "Upcoming Gifts",
                    items = emptyList()
                ),
                GiftGroup(
                    header = "Gifts by Person",
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
        onAddGift = { navController.navigate("add_gift_flow") },
        onSuggest = { /* TODO AI suggest */ },
        onOpenGift = { id -> navController.navigate("gift_detail/$id") },
        onOpenLink = { id -> 
            // TODO: Get gift URL by ID and open in external browser
            println("Opening external link for gift $id")
        },
        onToggleShortlist = { /* TODO */ },
        onMarkPurchased = { /* TODO */ },
        onAssistAction = { /* TODO */ }
    )
}
