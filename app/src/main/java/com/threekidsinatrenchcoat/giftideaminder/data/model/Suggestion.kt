package com.threekidsinatrenchcoat.giftideaminder.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "suggestions",
    foreignKeys = [ForeignKey(
        entity = Gift::class,
        parentColumns = ["id"],
        childColumns = ["giftId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["giftId"])]
)
data class Suggestion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val giftId: Int,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)
