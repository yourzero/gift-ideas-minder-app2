package com.threekidsinatrenchcoat.giftideaminder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threekidsinatrenchcoat.giftideaminder.core.achievements.Achievement
import com.threekidsinatrenchcoat.giftideaminder.core.achievements.AchievementManager
import com.threekidsinatrenchcoat.giftideaminder.core.achievements.AchievementType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val achievementManager: AchievementManager
) : ViewModel() {
    
    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()
    
    private val _newlyUnlockedAchievements = MutableStateFlow<List<Achievement>>(emptyList())
    val newlyUnlockedAchievements: StateFlow<List<Achievement>> = _newlyUnlockedAchievements.asStateFlow()
    
    private val _availableAISuggestions = MutableStateFlow(0)
    val availableAISuggestions: StateFlow<Int> = _availableAISuggestions.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadAchievements()
        observeAchievements()
        loadAvailableAISuggestions()
    }
    
    private fun observeAchievements() {
        viewModelScope.launch {
            achievementManager.getAchievementsFlow().collect { achievements ->
                _achievements.value = achievements
            }
        }
    }
    
    private fun loadAchievements() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val achievements = achievementManager.getAllAchievements()
                _achievements.value = achievements
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun loadAvailableAISuggestions() {
        viewModelScope.launch {
            val count = achievementManager.getAvailableAISuggestions()
            _availableAISuggestions.value = count
        }
    }
    
    fun checkAndUnlockAchievements() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newlyUnlocked = achievementManager.checkAndUnlockAchievements()
                _newlyUnlockedAchievements.value = newlyUnlocked
                loadAvailableAISuggestions() // Refresh AI suggestions count
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun unlockAchievement(type: AchievementType) {
        viewModelScope.launch {
            val unlocked = achievementManager.unlockAchievement(type)
            if (unlocked) {
                loadAchievements()
                loadAvailableAISuggestions()
            }
        }
    }
    
    fun useAISuggestion(): Boolean {
        return if (_availableAISuggestions.value > 0) {
            viewModelScope.launch {
                val canUse = achievementManager.useAISuggestion()
                if (canUse) {
                    loadAvailableAISuggestions() // Refresh count
                }
            }
            true
        } else {
            false
        }
    }
    
    fun clearNewlyUnlockedAchievements() {
        _newlyUnlockedAchievements.value = emptyList()
    }
}