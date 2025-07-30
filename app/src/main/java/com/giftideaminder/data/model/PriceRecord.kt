package com.giftideaminder.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "price_records",
    foreignKeys = [ForeignKey(
        entity = Gift::class,
        parentColumns = ["id"],
        childColumns = ["giftId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class PriceRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val giftId: Int,
    val timestamp: Long,
    val price: Double
)
