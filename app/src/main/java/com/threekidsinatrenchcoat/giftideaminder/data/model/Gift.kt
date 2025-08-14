package com.threekidsinatrenchcoat.giftideaminder.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(
    tableName = "gifts",
    foreignKeys = [ForeignKey(
        entity = Person::class,
        parentColumns = ["id"],
        childColumns = ["personId"],
        onDelete = ForeignKey.SET_NULL
    )],
    indices = [Index(value = ["personId"])]
)
@TypeConverters(Converters::class)
data class Gift(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String? = null,
    val url: String? = null,
    val asin: String? = null,
    val currencyCode: String = "USD",
    val originalPrice: Double? = null,
    val currentPrice: Double? = null,
    val purchasePrice: Double? = null,
    val purchaseDate: Long? = null,
    val eventDate: Long? = null,
    val personId: Int? = null,
    val reminderEnabled: Boolean = true,
    val reminderOffset: Int = 7,
    val saleAlertEnabled: Boolean = false,
    val saleAlertThreshold: Double? = null,
    val budget: Double? = null,
    val isPurchased: Boolean = false,
    val importSource: String? = null,
    val importDate: Long? = null,
    val category: String? = null,
    val tags: List<String>? = null
)
