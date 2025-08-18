// app/src/main/java/com/threekidsinatrenchcoat/giftideaminder/data/dao/InterestDao.kt
package com.threekidsinatrenchcoat.giftideaminder.data.dao

import androidx.room.*
import com.threekidsinatrenchcoat.giftideaminder.data.model.Interest
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
}