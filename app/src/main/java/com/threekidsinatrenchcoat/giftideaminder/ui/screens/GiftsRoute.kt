package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.threekidsinatrenchcoat.giftideaminder.data.model.Gift
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.GiftViewModel
import java.text.DateFormat
import java.util.Date

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
    val allGifts by viewModel.allGifts.collectAsState(initial = emptyList())
    val peopleById by viewModel.peopleById.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val isLoadingSuggestions by viewModel.isLoadingSuggestions.collectAsState()
    val suggestionsError by viewModel.suggestionsError.collectAsState()

    // Transform Gift entities to UI models
    val upcoming = remember(allGifts) {
        allGifts
            .filter { gift -> 
                // Show gifts with upcoming event dates (within next 30 days)
                gift.eventDate?.let { eventDate ->
                    val now = System.currentTimeMillis()
                    val thirtyDaysFromNow = now + (30 * 24 * 60 * 60 * 1000L)
                    eventDate in now..thirtyDaysFromNow
                } ?: false
            }
            .map { gift -> gift.toGiftCardUi(peopleById) }
    }

    val peopleSections = remember(allGifts, peopleById, suggestions) {
        // Group gifts by person
        val giftsByPerson = allGifts
            .filter { it.personId != null }
            .groupBy { it.personId!! }

        // Create person sections
        giftsByPerson.map { (personId, gifts) ->
            val personName = peopleById[personId] ?: "Unknown Person"
            PersonGiftsUi(
                personId = personId.toString(),
                personName = personName,
                gifts = gifts.map { it.toGiftCardUi(peopleById) },
                suggestions = suggestions
                    .filter { it.personId == personId }
                    .map { it.toSuggestionUi() }
            )
        }
    }

    // Load suggestions on first composition
    LaunchedEffect(Unit) {
        viewModel.fetchSuggestions()
    }

    GiftsScreen(
        upcoming = upcoming,
        peopleSections = peopleSections,
        onOpenLink = { url -> 
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                navController.context.startActivity(intent)
            } catch (e: Exception) {
                // Handle error - could show a toast or snackbar
                println("Failed to open URL: $url")
            }
        },
        onAcceptSuggestion = { suggestionId -> 
            // Find the suggestion and convert to gift
            suggestions.find { it.id.toString() == suggestionId }?.let { suggestion ->
                viewModel.onSaveGift(
                    title = suggestion.title,
                    description = suggestion.description ?: "",
                    url = suggestion.url ?: "",
                    price = suggestion.currentPrice?.toString() ?: "",
                    eventDate = suggestion.eventDate ?: -1L,
                    selectedPersonId = suggestion.personId
                )
                viewModel.dismissSuggestion(suggestion)
            }
        },
        onDismissSuggestion = { suggestionId ->
            suggestions.find { it.id.toString() == suggestionId }?.let { suggestion ->
                viewModel.dismissSuggestion(suggestion)
            }
        }
    )
}

// Extension functions to convert data models to UI models
private fun Gift.toGiftCardUi(peopleById: Map<Int, String>): GiftCardUi {
    val dateLabel = eventDate?.let { 
        val formatter = DateFormat.getDateInstance(DateFormat.SHORT)
        formatter.format(Date(it))
    }
    
    val personName = personId?.let { peopleById[it] }
    val subtitle = when {
        personName != null && currentPrice != null -> "$personName • $${String.format("%.2f", currentPrice)}"
        personName != null -> personName
        currentPrice != null -> "$${String.format("%.2f", currentPrice)}"
        else -> description
    }
    
    return GiftCardUi(
        id = id.toString(),
        title = title,
        subtitle = subtitle,
        dateLabel = dateLabel,
        imageUrl = null, // Could be extracted from URL or stored separately
        linkUrl = url
    )
}

private fun Gift.toSuggestionUi(): SuggestionUi {
    val priceLabel = currentPrice?.let { "$${String.format("%.2f", it)}" }
    
    return SuggestionUi(
        id = id.toString(),
        title = title,
        priceLabel = priceLabel,
        imageUrl = null, // Could be extracted from URL
        linkUrl = url
    )
}
