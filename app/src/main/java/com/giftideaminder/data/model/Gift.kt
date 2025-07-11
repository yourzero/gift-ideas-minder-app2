package com.giftideaminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gifts")
data class Gift(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String? = null,
    val url: String? = null,
    val price: Double? = null,
    val eventDate: Long? = null,
    val personId: Int? = null
) 