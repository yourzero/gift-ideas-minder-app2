package com.threekidsinatrenchcoat.giftideaminder.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.threekidsinatrenchcoat.giftideaminder.ui.state.GiftStatus
import com.threekidsinatrenchcoat.giftideaminder.ui.state.GiftUi

@Composable
fun GiftCard(
    item: GiftUi,
    onOpen: () -> Unit,
    onOpenLink: () -> Unit,
    onToggleShortlist: () -> Unit,
    onMarkPurchased: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onOpen,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            // Placeholder thumbnail (wire Coil later)
            Box(
                Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    item.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item.persons.take(2).forEach { AssistChip(onClick = {}, label = { Text(it) }) }
                    item.events.take(2).forEach { AssistChip(onClick = {}, label = { Text(it) }) }
                }
                Spacer(Modifier.height(6.dp))
                PriceRow(item.currentPrice, item.targetPrice, item.onSale)
                Spacer(Modifier.height(8.dp))
                StatusPill(item.status)
            }
            Spacer(Modifier.width(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onOpenLink, enabled = item.linkUrl != null) {
                    Icon(Icons.Default.Link, contentDescription = "Open link")
                }
                IconButton(onClick = onToggleShortlist) {
                    Icon(Icons.Default.Star, contentDescription = "Shortlist")
                }
                IconButton(onClick = onMarkPurchased) {
                    Icon(Icons.Default.Check, contentDescription = "Purchased")
                }
            }
        }
    }
}

@Composable
private fun PriceRow(current: Double?, target: Double?, onSale: Boolean) {
    val text = when {
        current == null && target == null -> "No price"
        current != null && target == null -> "$" + "%.2f".format(current)
        current == null && target != null -> "Target $" + "%.2f".format(target)
        else -> "$" + "%.2f".format(current!!) + "  (target $" + "%.2f".format(target!!) + ")"
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text, style = MaterialTheme.typography.bodyMedium)
        if (onSale) {
            Spacer(Modifier.width(8.dp))
            AssistChip(onClick = {}, label = { Text("On sale") })
        }
    }
}

@Composable
private fun StatusPill(status: GiftStatus) {
    val label = when (status) {
        GiftStatus.IDEA -> "Idea"
        GiftStatus.SHORTLIST -> "Shortlist"
        GiftStatus.PURCHASED -> "Purchased"
        GiftStatus.ARCHIVED -> "Archived"
    }
    AssistChip(onClick = { }, label = { Text(label) })
}
