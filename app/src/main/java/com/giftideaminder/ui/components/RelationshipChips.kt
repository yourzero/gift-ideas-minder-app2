package com.giftideaminder.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.giftideaminder.data.model.RelationshipType

@Composable
fun RelationshipChips(
    types: List<RelationshipType>,
    selected: String?,
    onSelect: (String) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Text("Relationship", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(types) { t ->
                AssistChip(
                    onClick = { onSelect(t.name) },
                    label = { Text(t.name) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (selected == t.name)
                            MaterialTheme.colorScheme.primaryContainer else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }
    }
}
