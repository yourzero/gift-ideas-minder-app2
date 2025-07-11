package com.giftideaminder.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.giftideaminder.data.model.Person
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy
import androidx.room.Update
import androidx.room.Delete

@Dao
interface PersonDao {
    @Query("SELECT * FROM persons")
    fun getAllPersons(): Flow<List<Person>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(person: Person)

    @Update
    suspend fun update(person: Person)

    @Delete
    suspend fun delete(person: Person)

    @Query("SELECT * FROM persons WHERE id = :id")
    fun getPersonById(id: Int): Flow<Person>
} 