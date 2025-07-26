package com.giftideaminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.giftideaminder.data.converter.PriceHistoryConverter

@Entity(tableName = "gifts")
@TypeConverters(PriceHistoryConverter::class)
data class Gift(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String? = null,
    val url: String? = null,
    val price: Double? = null,
    val eventDate: Long? = null,
    val personId: Int? = null,
    val reminderOffset: Int = 7,
    val currentPrice: Double? = null,
    val budget: Double? = null,
    val isPurchased: Boolean = false,
    val priceHistory: List<Pair<String, Double>>? = null,
    val purchaseDate: Long? = null,      // <-- new
) 