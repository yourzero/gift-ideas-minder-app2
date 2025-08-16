package com.threekidsinatrenchcoat.giftideaminder.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.threekidsinatrenchcoat.giftideaminder.ui.state.AssistItem

@Composable
fun AssistShelf(
    items: List<AssistItem>,
    onAction: (AssistItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return

    Column(modifier.fillMaxWidth()) {
        Text(
            "Assist",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items, key = { it.id }) { itx ->
                ElevatedCard {
                    Column(Modifier.padding(12.dp)) {
                        Text(itx.title, style = MaterialTheme.typography.titleSmall)
                        if (itx.subtitle != null) {
                            Spacer(Modifier.height(4.dp))
                            Text(itx.subtitle, style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { onAction(itx) }) {
                            Text(itx.ctaLabel)
                        }
                    }
                }
            }
        }
    }
}
