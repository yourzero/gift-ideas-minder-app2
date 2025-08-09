package com.giftideaminder.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.giftideaminder.data.model.RelationshipType
import kotlinx.coroutines.flow.Flow

@Dao
interface RelationshipTypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(types: List<RelationshipType>)

    @Query("SELECT * FROM relationship_types ORDER BY name ASC")
    fun getAll(): Flow<List<RelationshipType>>

    @Query("SELECT COUNT(*) FROM relationship_types")
    suspend fun count(): Int
}

