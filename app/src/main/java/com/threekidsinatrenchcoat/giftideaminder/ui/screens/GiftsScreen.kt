// app/src/main/java/com/threekidsinatrenchcoat/giftideaminder/ui/screens/GiftsScreen.kt
package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.threekidsinatrenchcoat.giftideaminder.ui.components.SuggestionsCarousel

/**
 * Minimal, VM-agnostic Gifts screen that satisfies todo.md:
 * - Sections: Upcoming Gifts, Gifts by Person
 * - Show suggestions ONLY under Gifts by Person (per-person suggestions)
 * - Suggestion cards show image (via Coil) and provide an Open Link action
 *
 * Wire your VM to feed [upcoming] and [peopleSections].
 */
@Composable
fun GiftsScreen(
    upcoming: List<GiftCardUi>,
    peopleSections: List<PersonGiftsUi>,
    onOpenLink: (String) -> Unit,
    onAcceptSuggestion: (String) -> Unit,
    onDismissSuggestion: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Upcoming section
        item {
            Text("Upcoming Gifts", style = MaterialTheme.typography.titleLarge)
        }
        if (upcoming.isEmpty()) {
            item {
                Text(
                    "Nothing upcoming. Add a gift or a date to get reminders.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(upcoming, key = { it.id }) { card ->
                GiftCardRow(card = card, onOpenLink = onOpenLink)
            }
        }

        // Gifts by person
        item {
            Spacer(Modifier.height(8.dp))
            Text("Gifts by Person", style = MaterialTheme.typography.titleLarge)
        }
        items(peopleSections, key = { it.personId }) { person ->
            PersonSection(
                person = person,
                onOpenLink = onOpenLink,
                onAcceptSuggestion = onAcceptSuggestion,
                onDismissSuggestion = onDismissSuggestion
            )
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun PersonSection(
    person: PersonGiftsUi,
    onOpenLink: (String) -> Unit,
    onAcceptSuggestion: (String) -> Unit,
    onDismissSuggestion: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(person.personName, style = MaterialTheme.typography.titleMedium)

        if (person.gifts.isEmpty()) {
            Text(
                "No saved gifts yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            person.gifts.forEach { card ->
                GiftCardRow(card = card, onOpenLink = onOpenLink)
            }
        }

        if (person.suggestions.isNotEmpty()) {
            // Suggestions appear ONLY under a person
            SuggestionsCarousel(
                suggestions = person.suggestions,
                onOpenLink = onOpenLink,
                onAccept = onAcceptSuggestion,
                onDismiss = onDismissSuggestion
            )
        }
        Divider(Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun GiftCardRow(
    card: GiftCardUi,
    onOpenLink: (String) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = card.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(56.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(card.title, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (card.subtitle != null) {
                    Text(card.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                }
                if (card.dateLabel != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(card.dateLabel, style = MaterialTheme.typography.bodySmall)
                }
            }
            if (!card.linkUrl.isNullOrBlank()) {
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = { onOpenLink(card.linkUrl!!) }) {
                    Text("Open Link")
                }
            }
        }
    }
}

/** --- Simple UI models so this file is drop-in regardless of your data layer --- */
data class GiftCardUi(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val dateLabel: String? = null,
    val imageUrl: String? = null,
    val linkUrl: String? = null
)

data class SuggestionUi(
    val id: String,
    val title: String,
    val priceLabel: String? = null,
    val imageUrl: String? = null,
    val linkUrl: String? = null
)

data class PersonGiftsUi(
    val personId: String,
    val personName: String,
    val gifts: List<GiftCardUi>,
    val suggestions: List<SuggestionUi>
)
