package com.threekidsinatrenchcoat.giftideaminder.data.repository

import com.threekidsinatrenchcoat.giftideaminder.data.dao.PersonDao
import com.threekidsinatrenchcoat.giftideaminder.data.model.Person
import kotlinx.coroutines.flow.Flow

class PersonRepository(private val personDao: PersonDao) {
    val allPersons: Flow<List<Person>> = personDao.getAllPersons()

    suspend fun insert(person: Person): Int {
        return personDao.insert(person).toInt()
    }

    suspend fun update(person: Person) {
        personDao.update(person)
    }

    suspend fun delete(person: Person) {
        personDao.delete(person)
    }

    fun getPersonById(id: Int): Flow<Person> = personDao.getPersonById(id)

    suspend fun getPersonByIdSuspend(id: Int): Person? = personDao.getPersonByIdSuspend(id)

    suspend fun getPeopleWithRole(roleBit: Int): List<Person> = personDao.getPeopleWithRole(roleBit)
} 