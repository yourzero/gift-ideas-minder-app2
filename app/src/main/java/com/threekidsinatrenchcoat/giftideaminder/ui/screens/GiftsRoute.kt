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
        upcoming = emptyList(), // TODO: Map from actual gifts data
        peopleSections = emptyList(), // TODO: Map from actual people and their gifts
        onOpenLink = { url -> 
            // TODO: Open URL in external browser
            println("Opening external link: $url")
        },
        onAcceptSuggestion = { suggestionId -> 
            // TODO: Accept suggestion and add to gifts
            println("Accepting suggestion: $suggestionId")
        },
        onDismissSuggestion = { suggestionId ->
            // TODO: Dismiss suggestion
            println("Dismissing suggestion: $suggestionId")
        }
    )
}
