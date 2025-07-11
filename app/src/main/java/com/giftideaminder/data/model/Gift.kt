package com.giftideaminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gifts")
data class Gift(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val url: String = "",
    val price: Double = 0.0,
    val eventDate: Long = 0L,
    val personId: Int? = null
) 