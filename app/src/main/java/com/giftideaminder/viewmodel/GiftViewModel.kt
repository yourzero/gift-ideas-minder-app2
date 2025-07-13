package com.giftideaminder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giftideaminder.data.model.Gift
import com.giftideaminder.data.repository.GiftRepository
import com.giftideaminder.data.api.PriceService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GiftViewModel @Inject constructor(
    private val giftRepository: GiftRepository,
    private val priceService: PriceService
) : ViewModel() {

    val allGifts: Flow<List<Gift>> = giftRepository.allGifts

    fun insertGift(gift: Gift) = viewModelScope.launch {
        giftRepository.insert(gift)
    }

    fun updateGift(gift: Gift) = viewModelScope.launch {
        giftRepository.update(gift)
    }

    fun deleteGift(gift: Gift) = viewModelScope.launch {
        giftRepository.delete(gift)
    }

    fun getGiftById(id: Int): Flow<Gift> = giftRepository.getGiftById(id)

    fun updatePriceForGift(gift: Gift) = viewModelScope.launch {
        gift.url?.let { url ->
            val asin = extractAsinFromUrl(url) ?: return@launch
            try {
                val response = priceService.getPriceHistory(asin, "YOUR_API_KEY")
                val history = response.price_history.map { it.date to it.price }
                val updatedGift = gift.copy(
                    currentPrice = response.current_price,
                    priceHistory = history
                )
                giftRepository.update(updatedGift)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun extractAsinFromUrl(url: String): String? {
        return Regex("/([A-Z0-9]{10})(?:/|\$)").find(url)?.groupValues?.get(1)
    }
} 