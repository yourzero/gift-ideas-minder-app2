package com.threekidsinatrenchcoat.giftideaminder.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.threekidsinatrenchcoat.giftideaminder.data.model.InterestEntity

/**
 * A compact chip component for displaying interest details with owned toggle and delete action.
 * Displays the interest label with action buttons in a horizontal chip layout.
 *
 * @param interestEntity The interest entity to display
 * @param onToggleOwned Callback when the owned status is toggled (id, newOwnedStatus)
 * @param onDelete Callback when the delete action is triggered
 * @param modifier Modifier for styling
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailChip(
    interestEntity: InterestEntity,
    onToggleOwned: (Int, Boolean) -> Unit,
    onDelete: (InterestEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = if (interestEntity.isOwned) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        contentColor = if (interestEntity.isOwned) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Interest label
            Text(
                text = interestEntity.label,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            
            // Owned toggle button
            IconButton(
                onClick = { onToggleOwned(interestEntity.id, !interestEntity.isOwned) },
                modifier = Modifier.size(24.dp)
            ) {
                Text(
                    text = if (interestEntity.isOwned) "❤️" else "🚫",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            
            // Delete button
            IconButton(
                onClick = { onDelete(interestEntity) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete ${interestEntity.label}",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}