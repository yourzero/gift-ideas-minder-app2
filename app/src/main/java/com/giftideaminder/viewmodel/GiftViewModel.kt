package com.giftideaminder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giftideaminder.data.model.Gift
import com.giftideaminder.data.repository.GiftRepository
import com.giftideaminder.data.api.PriceService
import com.giftideaminder.data.api.AIService
import com.giftideaminder.data.api.AIRequest
import com.giftideaminder.data.repository.PersonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GiftViewModel @Inject constructor(
    private val giftRepository: GiftRepository,
    private val priceService: PriceService,
    private val personRepository: PersonRepository,
    private val aiService: AIService
) : ViewModel() {

    // Current user name for personalization
    private val _currentUserName = MutableStateFlow("Guest")
    val currentUserName: StateFlow<String> = _currentUserName.asStateFlow()

    // All stored gifts
    val allGifts: Flow<List<Gift>> = giftRepository.allGifts

    // AI suggestions
    private val _suggestions = MutableStateFlow<List<Gift>>(emptyList())
    val suggestions: StateFlow<List<Gift>> = _suggestions.asStateFlow()

    init {
        fetchSuggestions()
    }

    /** Update the current user name **/
    fun setCurrentUserName(name: String) {
        _currentUserName.value = name
    }

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

    fun fetchSuggestions() = viewModelScope.launch {
        try {
            val allGiftsList = allGifts.first()
            val allPersons = personRepository.allPersons.first()
            val request = AIRequest(gifts = allGiftsList, persons = allPersons)
            val response = aiService.getSuggestions(request)
            _suggestions.value = response
        } catch (e: Exception) {
            // Handle error, e.g., _suggestions.value = emptyList()
        }
    }

    fun dismissSuggestion(suggestion: Gift) {
        _suggestions.value = _suggestions.value.filter { it != suggestion }
    }

    private fun extractAsinFromUrl(url: String): String? {
        return Regex("/([A-Z0-9]{10})(?:/|\$)")
            .find(url)
            ?.groupValues
            ?.get(1)
    }
}
