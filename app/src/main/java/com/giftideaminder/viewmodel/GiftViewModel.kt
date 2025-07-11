package com.giftideaminder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giftideaminder.data.model.Gift
import com.giftideaminder.data.repository.GiftRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GiftViewModel @Inject constructor(
    private val giftRepository: GiftRepository
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
} 