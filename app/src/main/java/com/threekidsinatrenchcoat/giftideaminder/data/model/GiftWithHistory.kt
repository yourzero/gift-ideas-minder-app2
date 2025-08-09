package com.threekidsinatrenchcoat.giftideaminder.data.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Combines a Gift entity with its list of PriceRecord history.
 */
data class GiftWithHistory(
    @Embedded
    val gift: Gift,
    @Relation(
        parentColumn = "id",
        entityColumn = "giftId"
    )
    val currentPriceHistory: List<PriceRecord>
)
