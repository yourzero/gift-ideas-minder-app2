package com.giftideaminder.data.repository

import com.giftideaminder.data.dao.ImportantDateDao
import com.giftideaminder.data.model.ImportantDate
import kotlinx.coroutines.flow.Flow

class ImportantDateRepository(private val dao: ImportantDateDao) {
    fun getForPerson(personId: Int): Flow<List<ImportantDate>> = dao.getForPerson(personId)
    suspend fun insert(date: ImportantDate) = dao.insert(date)
    suspend fun update(date: ImportantDate) = dao.update(date)
    suspend fun deleteById(id: Int) = dao.deleteById(id)
    suspend fun replaceForPerson(personId: Int, dates: List<ImportantDate>) = dao.replaceForPerson(personId, dates)
}

