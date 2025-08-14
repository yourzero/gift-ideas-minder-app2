package com.threekidsinatrenchcoat.giftideaminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "relationship_types")
data class RelationshipType(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val hasBirthday: Boolean = true,
    val hasAnniversary: Boolean = false
)
