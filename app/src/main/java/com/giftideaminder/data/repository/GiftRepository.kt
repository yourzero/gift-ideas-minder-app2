package com.giftideaminder.data.repository

import com.giftideaminder.data.dao.GiftDao
import com.giftideaminder.data.model.Gift
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
} 