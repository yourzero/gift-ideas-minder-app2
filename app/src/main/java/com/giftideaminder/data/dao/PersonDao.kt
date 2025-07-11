package com.giftideaminder.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.giftideaminder.data.model.Person
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Query("SELECT * FROM persons")
    fun getAllPersons(): Flow<List<Person>>

    @Insert
    suspend fun insert(person: Person)
} 