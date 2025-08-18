package com.threekidsinatrenchcoat.giftideaminder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threekidsinatrenchcoat.giftideaminder.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    // Interests advanced mode (default is true for backward compatibility)
    val isAdvancedMode: StateFlow<Boolean> = settingsRepository
        .getBooleanSetting(SettingsRepository.KEY_INTERESTS_ADVANCED_MODE, defaultValue = true)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    fun setAdvancedMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setBooleanSetting(
                SettingsRepository.KEY_INTERESTS_ADVANCED_MODE, 
                enabled
            )
        }
    }
}