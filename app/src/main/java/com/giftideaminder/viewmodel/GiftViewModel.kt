package com.giftideaminder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giftideaminder.data.dao.GiftDao
import com.giftideaminder.data.model.Gift
import com.giftideaminder.data.model.GiftWithHistory
import com.giftideaminder.data.model.PriceRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GiftViewModel @Inject constructor(
    private val giftDao: GiftDao
) : ViewModel() {

    val allGifts: Flow<List<Gift>> = giftDao.getAllGifts()

    /** Returns a Flow of Gift entities. */
    fun getGiftById(id: Int): Flow<Gift> = giftDao.getGiftById(id)

    /** Returns a Flow of Gift with its price history. */
    fun getGiftWithHistoryById(id: Int): Flow<GiftWithHistory> =
        giftDao.getGiftWithHistoryById(id)

    /** Inserts a new gift. */
    fun insertGift(gift: Gift) {
        viewModelScope.launch {
            giftDao.insert(gift)
        }
    }

    /** Inserts or updates a gift. */
    fun updateGift(gift: Gift) {
        viewModelScope.launch {
            giftDao.update(gift)
        }
    }

    /** Deletes a gift. */
    fun deleteGift(gift: Gift) {
        viewModelScope.launch {
            giftDao.delete(gift)
        }
    }

    /** Triggers price update for the gift. */
    fun updatePriceForGift(gift: Gift) {
        viewModelScope.launch {
            // TODO: Implement price update logic
        }
    }

    // Stubs for suggestions
    private val _suggestions = MutableStateFlow<List<Gift>>(emptyList())
    val suggestions = _suggestions.asStateFlow()

    fun fetchSuggestions() {
        // TODO: Implement fetching suggestions
    }

    fun dismissSuggestion(suggestion: Gift) {
        // TODO: Implement dismissing suggestion
    }
}
