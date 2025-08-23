package com.threekidsinatrenchcoat.giftideaminder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threekidsinatrenchcoat.giftideaminder.core.achievements.Achievement
import com.threekidsinatrenchcoat.giftideaminder.core.achievements.AchievementEvent
import com.threekidsinatrenchcoat.giftideaminder.core.achievements.AchievementManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class AchievementViewModel @Inject constructor(
    private val achievementManager: AchievementManager
) : ViewModel() {

    // ---------- UI State (SSOT pattern) ----------
    data class AchievementUiState(
        val achievements: List<Achievement> = emptyList(),
        val recentUnlocks: List<Achievement> = emptyList(),
        val unlockedFeatures: Set<String> = emptySet(),
        val showConfetti: Boolean = false,
        val celebrationMessage: String? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(AchievementUiState())
    val uiState: StateFlow<AchievementUiState> = _uiState.asStateFlow()

    // ---------- One-off UI events (snackbar/nav) ----------
    sealed interface AchievementViewModelEvent {
        data class ShowToast(val message: String) : AchievementViewModelEvent
        data class ShowSnackbar(val message: String) : AchievementViewModelEvent
        data object TriggerConfetti : AchievementViewModelEvent
    }
    private val _events = MutableSharedFlow<AchievementViewModelEvent>()
    val events: SharedFlow<AchievementViewModelEvent> = _events.asSharedFlow()

    init {
        // Auto-check achievements on init
        loadAchievements()
        
        // Listen to AchievementManager events
        viewModelScope.launch {
            achievementManager.achievementEvents.collect { event ->
                handleAchievementEvent(event)
            }
        }
    }

    // ---------- Public methods ----------
    
    /**
     * Load all achievements and check for unlocks
     */
    fun loadAchievements() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Check for new achievements first
                achievementManager.checkAchievements()
                
                // Then load all unlocked achievements
                val unlockedAchievements = achievementManager.getUnlockedAchievements()
                val featureUnlocks = setOf("ai_suggestions").filter { feature ->
                    achievementManager.isFeatureUnlocked(feature)
                }.toSet()
                
                _uiState.update {
                    it.copy(
                        achievements = unlockedAchievements,
                        unlockedFeatures = featureUnlocks,
                        isLoading = false
                    )
                }
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(
                        error = t.message ?: "Failed to load achievements",
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * Check for new achievements (can be called manually)
     */
    fun checkForNewAchievements() {
        viewModelScope.launch {
            try {
                achievementManager.checkAchievements()
            } catch (t: Throwable) {
                _uiState.update { 
                    it.copy(error = t.message ?: "Failed to check achievements") 
                }
            }
        }
    }

    /**
     * Dismiss the current celebration UI
     */
    fun dismissCelebration() {
        _uiState.update {
            it.copy(
                showConfetti = false,
                celebrationMessage = null,
                recentUnlocks = emptyList()
            )
        }
    }

    /**
     * Get feature unlock status for a specific feature
     */
    fun getFeatureStatus(feature: String): Boolean {
        return _uiState.value.unlockedFeatures.contains(feature)
    }

    /**
     * Mark Twenty Questions as complete (manual trigger)
     */
    fun markTwentyQComplete() {
        viewModelScope.launch {
            try {
                achievementManager.unlockAchievement("FIRST_TWENTY_Q")
            } catch (t: Throwable) {
                _uiState.update { 
                    it.copy(error = t.message ?: "Failed to unlock Twenty Q achievement") 
                }
            }
        }
    }

    /**
     * Mark gift as given (manual trigger)
     */
    fun markGiftGiven() {
        viewModelScope.launch {
            try {
                achievementManager.unlockAchievement("GIFT_GIVER")
            } catch (t: Throwable) {
                _uiState.update { 
                    it.copy(error = t.message ?: "Failed to unlock Gift Giver achievement") 
                }
            }
        }
    }

    /**
     * Clear any error state
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    // ---------- Private methods ----------

    /**
     * Handle events from AchievementManager
     */
    private suspend fun handleAchievementEvent(event: AchievementEvent) {
        when (event) {
            is AchievementEvent.Unlocked -> {
                handleAchievementUnlocked(event.achievement)
            }
            is AchievementEvent.FeatureUnlocked -> {
                handleFeatureUnlocked(event.feature, event.description)
            }
        }
    }

    /**
     * Handle a newly unlocked achievement
     */
    private suspend fun handleAchievementUnlocked(achievement: Achievement) {
        // Add to recent unlocks
        _uiState.update { state ->
            state.copy(
                achievements = state.achievements + achievement,
                recentUnlocks = state.recentUnlocks + achievement,
                showConfetti = true,
                celebrationMessage = "Achievement Unlocked: ${achievement.title}"
            )
        }

        // Emit UI events
        _events.emit(AchievementViewModelEvent.TriggerConfetti)
        _events.emit(AchievementViewModelEvent.ShowToast("🏆 ${achievement.title}"))
    }

    /**
     * Handle a newly unlocked feature
     */
    private suspend fun handleFeatureUnlocked(feature: String, description: String) {
        // Update unlocked features
        _uiState.update { state ->
            state.copy(
                unlockedFeatures = state.unlockedFeatures + feature,
                showConfetti = true,
                celebrationMessage = "Feature Unlocked: $description"
            )
        }

        // Emit UI events  
        _events.emit(AchievementViewModelEvent.TriggerConfetti)
        _events.emit(AchievementViewModelEvent.ShowSnackbar("🎉 $description"))
    }

    // ---------- Convenience methods for common feature checks ----------

    /**
     * Check if AI suggestions are unlocked
     */
    fun isAiSuggestionsUnlocked(): Boolean = getFeatureStatus("ai_suggestions")

    /**
     * Get count of unlocked achievements
     */
    fun getUnlockedCount(): Int = _uiState.value.achievements.size

    /**
     * Check if there are recent unlocks to display
     */
    fun hasRecentUnlocks(): Boolean = _uiState.value.recentUnlocks.isNotEmpty()
}