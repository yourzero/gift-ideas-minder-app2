package com.threekidsinatrenchcoat.giftideaminder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threekidsinatrenchcoat.giftideaminder.data.model.InterestEntity
import com.threekidsinatrenchcoat.giftideaminder.data.repository.InterestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterestsViewModel @Inject constructor(
    private val interestRepository: InterestRepository
) : ViewModel() {

    // ---------- UI State ----------
    data class InterestsUiState(
        val parentInterests: List<InterestEntity> = emptyList(),
        val childInterests: List<InterestEntity> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val aiSuggestions: List<String> = emptyList(),
        val isLoadingSuggestions: Boolean = false
    )

    private val _uiState = MutableStateFlow(InterestsUiState())
    val uiState: StateFlow<InterestsUiState> = _uiState.asStateFlow()

    // ---------- Loading Methods ----------
    
    /**
     * Load parent interests for a specific person
     */
    fun loadParentInterests(personId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                interestRepository.getParentInterests(personId).collect { interests ->
                    _uiState.update { 
                        it.copy(
                            parentInterests = interests,
                            isLoading = false
                        ) 
                    }
                }
            } catch (t: Throwable) {
                _uiState.update { 
                    it.copy(
                        error = t.message ?: "Failed to load parent interests",
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * Load child interests for a specific parent interest
     */
    fun loadChildInterests(parentId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                interestRepository.getChildInterests(parentId).collect { interests ->
                    _uiState.update { 
                        it.copy(
                            childInterests = interests,
                            isLoading = false
                        ) 
                    }
                }
            } catch (t: Throwable) {
                _uiState.update { 
                    it.copy(
                        error = t.message ?: "Failed to load child interests",
                        isLoading = false
                    )
                }
            }
        }
    }

    // ---------- Toggle Methods ----------
    
    /**
     * Toggle the owned flag for an interest
     */
    fun toggleOwned(id: Int, isOwned: Boolean) {
        viewModelScope.launch {
            try {
                interestRepository.toggleOwned(id, isOwned)
            } catch (t: Throwable) {
                _uiState.update { 
                    it.copy(error = t.message ?: "Failed to update owned status") 
                }
            }
        }
    }

    /**
     * Toggle the dislike flag for an interest
     */
    fun toggleDislike(id: Int, isDislike: Boolean) {
        viewModelScope.launch {
            try {
                interestRepository.toggleDislike(id, isDislike)
            } catch (t: Throwable) {
                _uiState.update { 
                    it.copy(error = t.message ?: "Failed to update dislike status") 
                }
            }
        }
    }

    // ---------- Add Methods ----------
    
    /**
     * Add a new parent interest
     */
    fun addParentInterest(personId: Int, label: String) {
        if (label.isBlank()) {
            _uiState.update { it.copy(error = "Interest label cannot be empty") }
            return
        }

        viewModelScope.launch {
            try {
                val newInterest = InterestEntity(
                    personId = personId,
                    parentId = null, // null indicates parent interest
                    label = label.trim()
                )
                interestRepository.insertInterestEntity(newInterest)
            } catch (t: Throwable) {
                _uiState.update { 
                    it.copy(error = t.message ?: "Failed to add parent interest") 
                }
            }
        }
    }

    /**
     * Add a new child detail to a parent interest
     */
    fun addChildDetail(parentId: Int, personId: Int, label: String) {
        if (label.isBlank()) {
            _uiState.update { it.copy(error = "Detail label cannot be empty") }
            return
        }

        viewModelScope.launch {
            try {
                val newDetail = InterestEntity(
                    personId = personId,
                    parentId = parentId, // non-null indicates child detail
                    label = label.trim()
                )
                interestRepository.insertInterestEntity(newDetail)
            } catch (t: Throwable) {
                _uiState.update { 
                    it.copy(error = t.message ?: "Failed to add child detail") 
                }
            }
        }
    }

    // ---------- AI Suggestions ----------
    
    /**
     * Get AI suggestions for child details based on parent label
     * Currently stubbed - to be implemented with AI integration
     */
    fun getAiSuggestions(parentLabel: String) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoadingSuggestions = true, 
                    error = null 
                ) 
            }
            
            try {
                // TODO: Implement AI integration to generate suggestions
                // For now, return some basic suggestions based on common interests
                val suggestions = generateBasicSuggestions(parentLabel)
                
                _uiState.update { 
                    it.copy(
                        aiSuggestions = suggestions,
                        isLoadingSuggestions = false
                    ) 
                }
            } catch (t: Throwable) {
                _uiState.update { 
                    it.copy(
                        error = t.message ?: "Failed to get AI suggestions",
                        isLoadingSuggestions = false
                    )
                }
            }
        }
    }

    /**
     * Clear AI suggestions
     */
    fun clearAiSuggestions() {
        _uiState.update { it.copy(aiSuggestions = emptyList()) }
    }

    /**
     * Clear any error messages
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    // ---------- Private Helper Methods ----------
    
    /**
     * Generate basic suggestions based on parent label
     * This is a temporary implementation until AI integration is added
     */
    private fun generateBasicSuggestions(parentLabel: String): List<String> {
        return when (parentLabel.lowercase().trim()) {
            "music" -> listOf("Rock", "Pop", "Jazz", "Classical", "Hip-hop", "Electronic")
            "sports" -> listOf("Football", "Basketball", "Soccer", "Tennis", "Baseball", "Hockey")
            "movies" -> listOf("Action", "Comedy", "Drama", "Sci-fi", "Horror", "Documentary")
            "books" -> listOf("Fiction", "Non-fiction", "Mystery", "Romance", "Biography", "Fantasy")
            "food" -> listOf("Italian", "Chinese", "Mexican", "Indian", "Thai", "Japanese")
            "hobbies" -> listOf("Photography", "Cooking", "Gardening", "Painting", "Gaming", "Crafting")
            "travel" -> listOf("Beach destinations", "Mountains", "Cities", "Camping", "Cruises", "Adventure trips")
            "technology" -> listOf("Smartphones", "Laptops", "Gaming", "Smart home", "Wearables", "Apps")
            else -> listOf("Specific brands", "Favorite styles", "Preferred types", "Recent interests", "Collections", "Seasonal preferences")
        }
    }
}