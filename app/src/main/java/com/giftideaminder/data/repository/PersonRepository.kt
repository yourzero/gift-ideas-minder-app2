package com.giftideaminder.data.repository

import com.giftideaminder.data.dao.PersonDao
import com.giftideaminder.data.model.Person
import kotlinx.coroutines.flow.Flow

class PersonRepository(private val personDao: PersonDao) {
    val allPersons: Flow<List<Person>> = personDao.getAllPersons()

    suspend fun insert(person: Person) {
        personDao.insert(person)
    }
} 