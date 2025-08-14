package com.threekidsinatrenchcoat.giftideaminder.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import com.threekidsinatrenchcoat.giftideaminder.data.model.ImportantDate
import kotlinx.coroutines.flow.Flow

@Dao
interface ImportantDateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(date: ImportantDate)

    @Update
    suspend fun update(date: ImportantDate)

    @Query("DELETE FROM important_dates WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM important_dates WHERE personId = :personId ORDER BY date ASC")
    fun getForPerson(personId: Int): Flow<List<ImportantDate>>

    @Query("SELECT * FROM important_dates")
    fun getAll(): Flow<List<ImportantDate>>

    @Query("DELETE FROM important_dates WHERE personId = :personId")
    suspend fun deleteForPerson(personId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(dates: List<ImportantDate>)

    @Transaction
    suspend fun replaceForPerson(personId: Int, dates: List<ImportantDate>) {
        deleteForPerson(personId)
        if (dates.isNotEmpty()) upsertAll(dates)
    }
}

