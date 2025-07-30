package com.giftideaminder.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "suggestions",
    foreignKeys = [ForeignKey(
        entity = Gift::class,
        parentColumns = ["id"],
        childColumns = ["giftId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Suggestion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val giftId: Int,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)
