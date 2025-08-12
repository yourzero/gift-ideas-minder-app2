package com.threekidsinatrenchcoat.giftideaminder.ui.components

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.threekidsinatrenchcoat.giftideaminder.data.model.Gift
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import coil.compose.AsyncImage

@Composable
fun SuggestionsCarousel(
    suggestions: StateFlow<List<Gift>>,
    onAccept: (Gift) -> Unit,
    onDismiss: (Gift) -> Unit,
    isLoading: StateFlow<Boolean>? = null,
    error: StateFlow<String?>? = null
) {
    val suggestionList = suggestions.collectAsState().value
    val loading = isLoading?.collectAsState()?.value ?: false
    val err = error?.collectAsState()?.value
    if (loading) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            CircularProgressIndicator()
        }
    } else if (!err.isNullOrBlank()) {
        Text("Failed to load suggestions: $err")
    } else if (suggestionList.isNotEmpty()) {
        Text("Today’s Suggestions")
        LazyRow(modifier = Modifier.semantics { contentDescription = "Suggestions carousel" }) {
            items(suggestionList) { suggestion: Gift ->
                Card(modifier = Modifier.padding(8.dp).semantics { contentDescription = "Suggestion: ${suggestion.title}" }) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        if (!suggestion.url.isNullOrBlank()) {
                            AsyncImage(
                                model = suggestion.url,
                                contentDescription = suggestion.title,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text("[Image Placeholder]")
                        }
                        Text(suggestion.title)
                        Text(suggestion.description ?: "")
                        Text("Est. Price: $${suggestion.currentPrice ?: "N/A"}")
                        Row {
                            Button(onClick = { onAccept(suggestion) }) { Text("Accept") }
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = { onDismiss(suggestion) }) { Text("Dismiss") }
                        }
                    }
                }
            }
        }
    } else {
        Text("No suggestions yet—add more gifts!")
    }
} 