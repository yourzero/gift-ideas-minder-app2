package com.threekidsinatrenchcoat.giftideaminder.data.dao

import androidx.room.*
import com.threekidsinatrenchcoat.giftideaminder.data.model.Person
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(person: Person): Long

    @Update
    suspend fun update(person: Person)

    @Delete
    suspend fun delete(person: Person)

    @Query("SELECT * FROM persons")
    fun getAllPersons(): Flow<List<Person>>

    @Query("SELECT * FROM persons WHERE id = :id")
    fun getPersonById(id: Int): Flow<Person>

    @Query("SELECT * FROM persons WHERE id = :personId LIMIT 1")
    suspend fun getPersonByIdSuspend(personId: Int): Person?

    @Query("SELECT * FROM persons WHERE (roles & :roleBit) != 0 ORDER BY name ASC")
    suspend fun getPeopleWithRole(roleBit: Int): List<Person>
    
    @Query("SELECT * FROM persons ORDER BY name ASC")
    suspend fun getAllPersonsSuspend(): List<Person>
}
