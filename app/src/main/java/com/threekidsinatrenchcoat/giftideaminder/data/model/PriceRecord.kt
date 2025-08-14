package com.threekidsinatrenchcoat.giftideaminder.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "price_records",
    foreignKeys = [ForeignKey(
        entity = Gift::class,
        parentColumns = ["id"],
        childColumns = ["giftId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["giftId"])]
)
data class PriceRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val giftId: Int,
    val timestamp: Long,
    val price: Double
)
