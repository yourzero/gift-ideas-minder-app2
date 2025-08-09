package com.threekidsinatrenchcoat.giftideaminder.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.LocalDate

@Entity(
    tableName = "important_dates",
    foreignKeys = [
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["personId"])])
@TypeConverters(Converters::class)
data class ImportantDate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val personId: Int,
    val label: String,
    val date: LocalDate
)
