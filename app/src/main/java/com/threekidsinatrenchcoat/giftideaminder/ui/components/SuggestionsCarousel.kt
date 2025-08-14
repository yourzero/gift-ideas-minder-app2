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
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import coil.compose.AsyncImage
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalContext
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

@Composable
fun SuggestionsCarousel(
    suggestions: StateFlow<List<Gift>>,
    onAccept: (Gift) -> Unit,
    onDismiss: (Gift) -> Unit,
    isLoading: StateFlow<Boolean>? = null,
    error: StateFlow<String?>? = null,
    personIdToName: Map<Int, String> = emptyMap()
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
                        val uriHandler = LocalUriHandler.current
                        
                        // Image display with proper sizing and fallback
                        if (!suggestion.url.isNullOrBlank()) {
                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(suggestion.url)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = suggestion.title,
                                modifier = Modifier
                                    .size(120.dp, 80.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { 
                                        // Open URL in external browser
                                        try {
                                            uriHandler.openUri(suggestion.url!!)
                                        } catch (e: Exception) {
                                            // Handle URL opening error silently
                                        }
                                    },
                                contentScale = ContentScale.Crop,
                                loading = {
                                    Box(
                                        modifier = Modifier.size(120.dp, 80.dp),
                                        contentAlignment = androidx.compose.ui.Alignment.Center
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                    }
                                },
                                error = {
                                    Box(
                                        modifier = Modifier.size(120.dp, 80.dp),
                                        contentAlignment = androidx.compose.ui.Alignment.Center
                                    ) {
                                        Text(
                                            "🎁",
                                            style = MaterialTheme.typography.headlineMedium
                                        )
                                    }
                                }
                            )
                        } else {
                            // Better placeholder with styling when no URL provided
                            Box(
                                modifier = Modifier
                                    .size(120.dp, 80.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                                    Text(
                                        "🎁",
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                    Text(
                                        "No Image",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        // Clickable title that opens URL
                        Text(
                            text = suggestion.title,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = if (!suggestion.url.isNullOrBlank()) {
                                Modifier.clickable { 
                                    try {
                                        uriHandler.openUri(suggestion.url!!)
                                    } catch (e: Exception) {
                                        // Handle URL opening error silently
                                    }
                                }
                            } else {
                                Modifier
                            },
                            color = if (!suggestion.url.isNullOrBlank()) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                        
                        val personName = suggestion.personId?.let { personIdToName[it] }
                        if (!personName.isNullOrBlank()) {
                            Text(
                                "For: $personName",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Text(
                            suggestion.description ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2
                        )
                        
                        Text(
                            "Est. Price: $${suggestion.currentPrice ?: "N/A"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
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