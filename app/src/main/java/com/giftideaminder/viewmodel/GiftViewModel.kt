package com.giftideaminder.viewmodel

import androidx.lifecycle.ViewModel
import com.giftideaminder.data.dao.GiftDao
import com.giftideaminder.data.model.Gift
import com.giftideaminder.data.model.GiftWithHistory
import com.giftideaminder.data.model.PriceRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class GiftViewModel @Inject constructor(
    private val giftDao: GiftDao
) : ViewModel() {

    /** Returns a Flow of Gift entities. */
    fun getGiftById(id: Int): Flow<Gift> = giftDao.getGiftById(id)

    /** Returns a Flow of Gift with its price history. */
    fun getGiftWithHistoryById(id: Int): Flow<GiftWithHistory> =
        giftDao.getGiftWithHistoryById(id)

    /** Inserts or updates a gift. */
    fun updateGift(gift: Gift) {
        // implementation omitted
    }

    /** Deletes a gift. */
    fun deleteGift(gift: Gift) {
        // implementation omitted
    }

    /** Triggers price update for the gift. */
    fun updatePriceForGift(gift: Gift) {
        // implementation omitted
    }
}
