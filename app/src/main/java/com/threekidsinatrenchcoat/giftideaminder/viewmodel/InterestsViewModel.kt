package com.threekidsinatrenchcoat.giftideaminder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threekidsinatrenchcoat.giftideaminder.data.model.InterestEntity
import com.threekidsinatrenchcoat.giftideaminder.data.repository.InterestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterestsViewModel @Inject constructor(
    private val interestRepository: InterestRepository
) : ViewModel() {
    
    private val _selectedPersonId = MutableStateFlow<Long?>(null)
    val selectedPersonId: StateFlow<Long?> = _selectedPersonId.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()
    
    fun setPersonId(personId: Long) {
        _selectedPersonId.value = personId
    }
    
    fun getParentInterests(personId: Long): Flow<List<InterestEntity>> {
        return interestRepository.getParentInterests(personId)
    }
    
    fun getChildInterests(parentId: Long): Flow<List<InterestEntity>> {
        return interestRepository.getChildInterests(parentId)
    }
    
    suspend fun getChildCount(parentId: Long): Int {
        return interestRepository.getChildCount(parentId)
    }
    
    fun addParentInterest(personId: Long, name: String, description: String? = null) {
        viewModelScope.launch {
            val interest = InterestEntity(
                personId = personId,
                name = name,
                description = description
            )
            interestRepository.insertInterestEntity(interest)
        }
    }
    
    fun addChildInterest(parentId: Long, personId: Long, name: String, description: String? = null) {
        viewModelScope.launch {
            val interest = InterestEntity(
                personId = personId,
                parentId = parentId,
                name = name,
                description = description
            )
            interestRepository.insertInterestEntity(interest)
        }
    }
    
    fun toggleOwned(interestId: Long, isOwned: Boolean) {
        viewModelScope.launch {
            interestRepository.toggleOwned(interestId, isOwned)
        }
    }
    
    fun toggleDislike(interestId: Long, isDislike: Boolean) {
        viewModelScope.launch {
            interestRepository.toggleDislike(interestId, isDislike)
        }
    }
    
    fun deleteInterest(interest: InterestEntity) {
        viewModelScope.launch {
            interestRepository.deleteInterestEntity(interest)
        }
    }
    
    fun generateSuggestions(parentName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            // Stub AI suggestions - map parent labels to canned suggestions
            val suggestions = when (parentName.lowercase()) {
                "books" -> listOf("Fiction novels", "Science fiction", "Biographies", "Cookbooks", "Technical manuals")
                "music" -> listOf("Rock", "Jazz", "Classical", "Electronic", "Indie")
                "sports" -> listOf("Basketball", "Soccer", "Tennis", "Swimming", "Running")
                "technology" -> listOf("Smartphones", "Laptops", "Gaming", "Smart home", "AI/ML")
                "cooking" -> listOf("Italian cuisine", "Baking", "Grilling", "Vegetarian", "Meal prep")
                "travel" -> listOf("Beach destinations", "Mountain hiking", "City breaks", "Cultural tours", "Adventure travel")
                else -> listOf("General interest", "Hobby items", "Collections", "Activities", "Experiences")
            }
            _suggestions.value = suggestions
            _isLoading.value = false
        }
    }
    
    fun clearSuggestions() {
        _suggestions.value = emptyList()
    }
}