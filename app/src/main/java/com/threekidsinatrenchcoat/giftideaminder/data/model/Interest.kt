// app/src/main/java/com/threekidsinatrenchcoat/giftideaminder/data/model/Interest.kt
package com.threekidsinatrenchcoat.giftideaminder.data.model

import androidx.room.*

@Entity(
    tableName = "interests",
    foreignKeys = [
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["personId"])]
)
data class Interest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val personId: Int,
    val type: InterestType,
    val value: String,
    val alreadyOwned: Boolean = false
)

enum class InterestType {
    GENERAL,    // General interests like "books", "coffee", "technology"
    SPECIFIC    // Specific items like "iPhone 15", "Harry Potter series"
}

@Entity(
    tableName = "interest_entities",
    foreignKeys = [
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["personId"]), 
        Index(value = ["parentId"])
    ]
)
data class InterestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val personId: Int,
    val parentId: Int? = null, // null for top-level interests, non-null for child details
    val label: String,
    val isDislike: Boolean = false,
    val isOwned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
