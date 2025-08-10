package com.giftideaminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "suggestion_dismissals")
data class SuggestionDismissal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val suggestionKey: String,
    val dismissedAt: Long = System.currentTimeMillis()
)

