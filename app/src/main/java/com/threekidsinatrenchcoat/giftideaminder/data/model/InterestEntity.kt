package com.threekidsinatrenchcoat.giftideaminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "interest_entities",
    foreignKeys = [
        ForeignKey(
            entity = InterestEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("parentId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Person::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("personId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["parentId"]),
        Index(value = ["personId"])
    ]
)
data class InterestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val personId: Long,
    val parentId: Long? = null,
    val name: String,
    val description: String? = null,
    val isDislike: Boolean = false,
    val isOwned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)