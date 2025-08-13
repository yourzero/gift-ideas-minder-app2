package com.threekidsinatrenchcoat.giftideaminder.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.threekidsinatrenchcoat.giftideaminder.ui.state.GiftFilters

@Composable
fun FilterChipsRow(
    filters: GiftFilters,
    onChange: (GiftFilters) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasAny = filters.persons.isNotEmpty() ||
        filters.events.isNotEmpty() ||
        filters.statuses.isNotEmpty() ||
        filters.stores.isNotEmpty() ||
        filters.minPrice != null ||
        filters.maxPrice != null ||
        filters.tags.isNotEmpty() ||
        filters.query.isNotBlank()

    Row(
        modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChip(
            onClick = { /* open bottom sheet later */ },
            label = { Text("Filters") },
            leadingIcon = {
                Icon(Icons.Default.FilterList, contentDescription = null)
            }
        )
        if (filters.persons.isNotEmpty()) {
            InputChip(selected = true, onClick = { /* open picker */ }, label = { Text("${filters.persons.size} person") })
        }
        if (filters.events.isNotEmpty()) {
            InputChip(selected = true, onClick = { /* open picker */ }, label = { Text("${filters.events.size} event") })
        }
        if (hasAny) {
            AssistChip(
                onClick = onReset,
                label = { Text("Reset") },
                leadingIcon = { Icon(Icons.Default.Clear, contentDescription = null) }
            )
        }
    }
}
