package com.giftideaminder.data.repository

import com.giftideaminder.data.dao.PersonDao
import com.giftideaminder.data.model.Person
import kotlinx.coroutines.flow.Flow

class PersonRepository(private val personDao: PersonDao) {
    val allPersons: Flow<List<Person>> = personDao.getAllPersons()

    suspend fun insert(person: Person) {
        personDao.insert(person)
    }

    suspend fun update(person: Person) {
        personDao.update(person)
    }

    suspend fun delete(person: Person) {
        personDao.delete(person)
    }

    fun getPersonById(id: Int): Flow<Person> = personDao.getPersonById(id)
} 