// app/src/main/java/com/threekidsinatrenchcoat/giftideaminder/data/repository/InterestRepository.kt
package com.threekidsinatrenchcoat.giftideaminder.data.repository

import com.threekidsinatrenchcoat.giftideaminder.data.dao.InterestDao
import com.threekidsinatrenchcoat.giftideaminder.data.model.Interest
import com.threekidsinatrenchcoat.giftideaminder.data.model.InterestEntity
import com.threekidsinatrenchcoat.giftideaminder.data.model.InterestType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterestRepository @Inject constructor(
    private val interestDao: InterestDao
) {
    
    fun getInterestsForPerson(personId: Int): Flow<List<Interest>> =
        interestDao.getInterestsForPerson(personId)
    
    fun getInterestsByType(personId: Int, type: InterestType): Flow<List<Interest>> =
        interestDao.getInterestsByType(personId, type)
    
    fun getAvailableInterestsForPerson(personId: Int): Flow<List<Interest>> =
        interestDao.getAvailableInterestsForPerson(personId)
    
    suspend fun insertInterest(interest: Interest) =
        interestDao.insert(interest)
    
    suspend fun insertInterests(interests: List<Interest>) =
        interestDao.insertAll(interests)
    
    suspend fun updateInterest(interest: Interest) =
        interestDao.update(interest)
    
    suspend fun deleteInterest(interest: Interest) =
        interestDao.delete(interest)
    
    suspend fun deleteInterestById(id: Int) =
        interestDao.deleteById(id)
    
    suspend fun deleteAllForPerson(personId: Int) =
        interestDao.deleteAllForPerson(personId)
    
    // InterestEntity methods
    fun getParentInterests(personId: Int): Flow<List<InterestEntity>> =
        interestDao.getParentInterests(personId)
    
    fun getChildInterests(parentId: Int): Flow<List<InterestEntity>> =
        interestDao.getChildInterests(parentId)
    
    fun getParentInterestsNonDislike(personId: Int): Flow<List<InterestEntity>> =
        interestDao.getParentInterestsNonDislike(personId)
    
    suspend fun getChildCount(parentId: Int): Int =
        interestDao.getChildCount(parentId)
    
    suspend fun insertInterestEntity(interestEntity: InterestEntity) =
        interestDao.insertEntity(interestEntity)
    
    suspend fun updateInterestEntity(interestEntity: InterestEntity) =
        interestDao.updateEntity(interestEntity)
    
    suspend fun deleteInterestEntity(interestEntity: InterestEntity) =
        interestDao.deleteEntity(interestEntity)
    
    suspend fun toggleOwned(id: Int, isOwned: Boolean) =
        interestDao.toggleOwned(id, isOwned)
    
    suspend fun toggleDislike(id: Int, isDislike: Boolean) =
        interestDao.toggleDislike(id, isDislike)
    
    suspend fun deleteAllEntitiesForPerson(personId: Int) =
        interestDao.deleteAllEntitiesForPerson(personId)
}