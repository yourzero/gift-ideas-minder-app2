package com.threekidsinatrenchcoat.giftideaminder.data.repository

import androidx.room.*
import com.threekidsinatrenchcoat.giftideaminder.data.model.RelationshipType
import kotlinx.coroutines.flow.Flow

@Dao
interface RelationshipTypeDao {
    @Query("SELECT * FROM relationship_types ORDER BY name ASC")
    fun getAll(): Flow<List<RelationshipType>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(type: RelationshipType): Long

    @Delete
    suspend fun delete(type: RelationshipType)

    @Query("SELECT * FROM relationship_types WHERE name = :name LIMIT 1")
    suspend fun findByName(name: String): RelationshipType?
}
