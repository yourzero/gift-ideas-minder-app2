package com.threekidsinatrenchcoat.giftideaminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.LocalDate

@Entity(tableName = "persons")
@TypeConverters(Converters::class)
data class Person(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val birthday: LocalDate? = null,
    val photoUri: String? = null,
    val relationships: List<String> = emptyList(),
    val notes: String? = null,
    val preferences: List<String> = emptyList(),
    val contactInfo: String? = null,
    val autoAssignFromSMS: Boolean = false,
    val defaultBudget: Double? = null,
    val roles: Int = PersonRole.RECIPIENT.bit
)
