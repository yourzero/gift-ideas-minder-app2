package com.giftideaminder.ui.components

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.giftideaminder.data.model.Gift
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.ui.unit.dp

@Composable
fun SuggestionsCarousel(
    suggestions: StateFlow<List<Gift>>,
    onAccept: (Gift) -> Unit,
    onDismiss: (Gift) -> Unit
) {
    val suggestionList = suggestions.collectAsState().value
    if (suggestionList.isNotEmpty()) {
        Text("Today’s Suggestions")
        LazyRow {
            items(suggestionList) { suggestion ->
                Card(modifier = Modifier.padding(8.dp)) {
                    Text(suggestion.title)
                    Text(suggestion.description ?: "")
                    Button(onClick = { onAccept(suggestion) }) {
                        Text("Accept")
                    }
                    Button(onClick = { onDismiss(suggestion) }) {
                        Text("Dismiss")
                    }
                }
            }
        }
    }
} 