// app/src/main/java/com/threekidsinatrenchcoat/giftideaminder/data/dao/InterestDao.kt
package com.threekidsinatrenchcoat.giftideaminder.data.dao

import androidx.room.*
import com.threekidsinatrenchcoat.giftideaminder.data.model.Interest
import com.threekidsinatrenchcoat.giftideaminder.data.model.InterestEntity
import com.threekidsinatrenchcoat.giftideaminder.data.model.InterestType
import kotlinx.coroutines.flow.Flow

@Dao
interface InterestDao {
    
    @Query("SELECT * FROM interests WHERE personId = :personId ORDER BY type, value")
    fun getInterestsForPerson(personId: Int): Flow<List<Interest>>
    
    @Query("SELECT * FROM interests WHERE personId = :personId AND type = :type ORDER BY value")
    fun getInterestsByType(personId: Int, type: InterestType): Flow<List<Interest>>
    
    @Query("SELECT * FROM interests WHERE personId = :personId AND alreadyOwned = 0 ORDER BY type, value")
    fun getAvailableInterestsForPerson(personId: Int): Flow<List<Interest>>
    
    @Insert
    suspend fun insert(interest: Interest)
    
    @Insert
    suspend fun insertAll(interests: List<Interest>)
    
    @Update
    suspend fun update(interest: Interest)
    
    @Delete
    suspend fun delete(interest: Interest)
    
    @Query("DELETE FROM interests WHERE personId = :personId")
    suspend fun deleteAllForPerson(personId: Int)
    
    @Query("DELETE FROM interests WHERE id = :id")
    suspend fun deleteById(id: Int)
    
    // New InterestEntity methods
    @Query("SELECT * FROM interest_entities WHERE personId = :personId AND parentId IS NULL ORDER BY name")
    fun getParentInterests(personId: Long): Flow<List<InterestEntity>>
    
    @Query("SELECT * FROM interest_entities WHERE parentId = :parentId ORDER BY name")
    fun getChildInterests(parentId: Long): Flow<List<InterestEntity>>
    
    @Query("SELECT * FROM interest_entities WHERE personId = :personId AND parentId IS NULL AND isDislike = 0 ORDER BY name")
    fun getParentInterestsNonDislike(personId: Long): Flow<List<InterestEntity>>
    
    @Query("SELECT COUNT(*) FROM interest_entities WHERE parentId = :parentId")
    suspend fun getChildCount(parentId: Long): Int
    
    @Insert
    suspend fun insertEntity(interestEntity: InterestEntity): Long
    
    @Update
    suspend fun updateEntity(interestEntity: InterestEntity)
    
    @Delete
    suspend fun deleteEntity(interestEntity: InterestEntity)
    
    @Query("UPDATE interest_entities SET isOwned = :isOwned WHERE id = :id")
    suspend fun toggleOwned(id: Long, isOwned: Boolean)
    
    @Query("UPDATE interest_entities SET isDislike = :isDislike WHERE id = :id")
    suspend fun toggleDislike(id: Long, isDislike: Boolean)
    
    @Query("DELETE FROM interest_entities WHERE personId = :personId")
    suspend fun deleteAllEntitiesForPerson(personId: Long)
}