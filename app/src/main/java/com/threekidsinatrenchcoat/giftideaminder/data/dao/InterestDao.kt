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
    @Query("SELECT * FROM interest_entities WHERE personId = :personId AND parentId IS NULL ORDER BY label")
    fun getParentInterests(personId: Int): Flow<List<InterestEntity>>
    
    @Query("SELECT * FROM interest_entities WHERE parentId = :parentId ORDER BY label")
    fun getChildInterests(parentId: Int): Flow<List<InterestEntity>>
    
    @Query("SELECT * FROM interest_entities WHERE personId = :personId AND parentId IS NULL AND isDislike = 0 ORDER BY label")
    fun getParentInterestsNonDislike(personId: Int): Flow<List<InterestEntity>>
    
    @Query("SELECT COUNT(*) FROM interest_entities WHERE parentId = :parentId")
    suspend fun getChildCount(parentId: Int): Int
    
    @Insert
    suspend fun insertEntity(interestEntity: InterestEntity)
    
    @Update
    suspend fun updateEntity(interestEntity: InterestEntity)
    
    @Delete
    suspend fun deleteEntity(interestEntity: InterestEntity)
    
    @Query("UPDATE interest_entities SET isOwned = :isOwned WHERE id = :id")
    suspend fun toggleOwned(id: Int, isOwned: Boolean)
    
    @Query("UPDATE interest_entities SET isDislike = :isDislike WHERE id = :id")
    suspend fun toggleDislike(id: Int, isDislike: Boolean)
    
    @Query("DELETE FROM interest_entities WHERE personId = :personId")
    suspend fun deleteAllEntitiesForPerson(personId: Int)
}