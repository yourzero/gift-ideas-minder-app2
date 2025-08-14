// app/src/main/java/com/threekidsinatrenchcoat/giftideaminder/ui/components/SuggestionsCarousel.kt
package com.threekidsinatrenchcoat.giftideaminder.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalUriHandler
import coil.compose.AsyncImage
import com.threekidsinatrenchcoat.giftideaminder.ui.screens.SuggestionUi

/**
 * Horizontally scrollable row of suggestion cards.
 * - Always shows image (Coil will fallback gracefully)
 * - "Open Link" uses LocalUriHandler so your app state isn't lost.
 */
@Composable
fun SuggestionsCarousel(
    suggestions: List<SuggestionUi>,
    onOpenLink: (String) -> Unit,
    onAccept: (String) -> Unit,
    onDismiss: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (suggestions.isEmpty()) return
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(suggestions, key = { it.id }) { s ->
            SuggestionCard(
                suggestion = s,
                onOpenLink = onOpenLink,
                onAccept = onAccept,
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
private fun SuggestionCard(
    suggestion: SuggestionUi,
    onOpenLink: (String) -> Unit,
    onAccept: (String) -> Unit,
    onDismiss: (String) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.width(240.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            AsyncImage(
                model = suggestion.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                suggestion.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (suggestion.priceLabel != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    suggestion.priceLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!suggestion.linkUrl.isNullOrBlank()) {
                    TextButton(onClick = { onOpenLink(suggestion.linkUrl!!) }) {
                        Text("Open Link")
                    }
                }
                OutlinedButton(onClick = { onDismiss(suggestion.id) }) { Text("Dismiss") }
                Button(onClick = { onAccept(suggestion.id) }) { Text("Accept") }
            }
        }
    }
}
