package com.threekidsinatrenchcoat.giftideaminder.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Block
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.threekidsinatrenchcoat.giftideaminder.data.model.InterestEntity
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.InterestsViewModel

@Composable
fun InterestRow(
    interest: InterestEntity,
    onInterestClick: () -> Unit,
    onToggleOwned: () -> Unit,
    onToggleDislike: () -> Unit,
    viewModel: InterestsViewModel
) {
    val childCount by produceState(initialValue = 0, interest.id) {
        value = viewModel.getChildCount(interest.id)
    }
    
    Card(
        onClick = onInterestClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (interest.isDislike) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = interest.name,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = if (interest.isDislike) TextDecoration.LineThrough else null,
                        color = if (interest.isDislike) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                    
                    if (!interest.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = interest.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    if (childCount > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "+$childCount detail${if (childCount > 1) "s" else ""}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                
                Row {
                    // Dislike toggle
                    IconButton(onClick = onToggleDislike) {
                        Icon(
                            imageVector = if (interest.isDislike) Icons.Default.Block else Icons.Default.Block,
                            contentDescription = if (interest.isDislike) "Remove dislike" else "Mark as dislike",
                            tint = if (interest.isDislike) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            }
                        )
                    }
                    
                    // Owned toggle
                    IconButton(onClick = onToggleOwned) {
                        Icon(
                            imageVector = if (interest.isOwned) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (interest.isOwned) "Mark as not owned" else "Mark as owned",
                            tint = if (interest.isOwned) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            }
                        )
                    }
                }
            }
        }
    }
}