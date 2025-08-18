// app/src/main/java/com/threekidsinatrenchcoat/giftideaminder/data/repository/InterestRepository.kt
package com.threekidsinatrenchcoat.giftideaminder.data.repository

import com.threekidsinatrenchcoat.giftideaminder.data.dao.InterestDao
import com.threekidsinatrenchcoat.giftideaminder.data.model.Interest
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
}