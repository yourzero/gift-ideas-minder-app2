package com.threekidsinatrenchcoat.giftideaminder.data.repository

import com.threekidsinatrenchcoat.giftideaminder.data.dao.GiftDao
import com.threekidsinatrenchcoat.giftideaminder.data.model.Gift
import kotlinx.coroutines.flow.Flow

class GiftRepository(private val giftDao: GiftDao) {
    val allGifts: Flow<List<Gift>> = giftDao.getAllGifts()

    suspend fun insert(gift: Gift) {
        giftDao.insert(gift)
    }

    suspend fun update(gift: Gift) {
        giftDao.update(gift)
    }

    suspend fun delete(gift: Gift) {
        giftDao.delete(gift)
    }

    fun getGiftById(id: Int): Flow<Gift> = giftDao.getGiftById(id)
    
    suspend fun getByPersonId(personId: Int): List<Gift> = giftDao.getGiftsByPersonIdSuspend(personId)
} 