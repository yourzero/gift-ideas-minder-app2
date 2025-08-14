package com.threekidsinatrenchcoat.giftideaminder.data.repository

import androidx.room.*
import com.threekidsinatrenchcoat.giftideaminder.data.model.ImportantDate
import kotlinx.coroutines.flow.Flow

@Dao
interface ImportantDateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(date: ImportantDate): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(dates: List<ImportantDate>): List<Long>

    @Query("SELECT * FROM important_dates WHERE personId = :personId ORDER BY date ASC")
    fun forPerson(personId: Int): Flow<List<ImportantDate>>

    @Query("DELETE FROM important_dates WHERE personId = :personId")
    suspend fun deleteForPerson(personId: Int)
}
