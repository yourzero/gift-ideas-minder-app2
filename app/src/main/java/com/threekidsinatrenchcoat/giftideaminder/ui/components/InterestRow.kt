package com.threekidsinatrenchcoat.giftideaminder.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.threekidsinatrenchcoat.giftideaminder.R
import com.threekidsinatrenchcoat.giftideaminder.data.local.entities.InterestEntity

/**
 * A composable that displays a single interest row with label, child count badge,
 * and dislike toggle functionality.
 *
 * @param interest The interest entity to display
 * @param childCount Number of child interests (shows badge if > 0)
 * @param onToggleDislike Callback when dislike status is toggled
 * @param onTap Callback when the row is tapped
 * @param modifier Modifier for styling
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestRow(
    interest: InterestEntity,
    childCount: Int,
    onToggleDislike: (Int, Boolean) -> Unit,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable { onTap() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Interest label
            Text(
                text = interest.name,
                style = MaterialTheme.typography.bodyLarge,
                color = if (interest.isDisliked) {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.weight(1f)
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Child count badge
                if (childCount > 0) {
                    Card(
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "+$childCount",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                
                // Dislike toggle button
                IconButton(
                    onClick = { onToggleDislike(interest.id, !interest.isDisliked) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (interest.isDisliked) Icons.Default.Block else Icons.Default.Favorite,
                        contentDescription = if (interest.isDisliked) {
                            stringResource(R.string.remove_dislike, interest.name)
                        } else {
                            stringResource(R.string.mark_as_disliked, interest.name)
                        },
                        tint = if (interest.isDisliked) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                }
            }
        }
    }
}