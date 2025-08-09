package com.giftideaminder.data.repository

import com.giftideaminder.data.dao.RelationshipTypeDao
import com.giftideaminder.data.model.RelationshipType
import kotlinx.coroutines.flow.Flow

class RelationshipTypeRepository(private val dao: RelationshipTypeDao) {
    fun getAll(): Flow<List<RelationshipType>> = dao.getAll()
    suspend fun ensureSeeded() {
        if (dao.count() == 0) {
            dao.insertAll(
                listOf(
                    RelationshipType(name = "Spouse", hasBirthday = true, hasAnniversary = true),
                    RelationshipType(name = "Partner", hasBirthday = true, hasAnniversary = true),
                    RelationshipType(name = "Parent", hasBirthday = true, hasAnniversary = false),
                    RelationshipType(name = "Child", hasBirthday = true, hasAnniversary = false),
                    RelationshipType(name = "Sibling", hasBirthday = true, hasAnniversary = false),
                    RelationshipType(name = "Friend", hasBirthday = true, hasAnniversary = false),
                    RelationshipType(name = "Coworker", hasBirthday = true, hasAnniversary = false)
                )
            )
        }
    }
}

