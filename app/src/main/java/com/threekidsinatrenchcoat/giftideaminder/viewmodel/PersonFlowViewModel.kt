package com.threekidsinatrenchcoat.giftideaminder.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threekidsinatrenchcoat.giftideaminder.data.model.Person
import com.threekidsinatrenchcoat.giftideaminder.data.repository.PersonRepository
import com.threekidsinatrenchcoat.giftideaminder.data.repository.ImportantDateRepository
import com.threekidsinatrenchcoat.giftideaminder.data.model.ImportantDate
import com.threekidsinatrenchcoat.giftideaminder.data.model.RelationshipType
import com.threekidsinatrenchcoat.giftideaminder.data.repository.RelationshipTypeRepository
import com.threekidsinatrenchcoat.giftideaminder.data.repository.SmsAnalysisRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Temporary cache for SMS analysis suggestions to pass between ViewModels
 */
object SmsAnalysisCache {
    private val suggestions = mutableMapOf<Int, List<com.threekidsinatrenchcoat.giftideaminder.data.model.Gift>>()
    
    fun storeSuggestions(personId: Int, giftSuggestions: List<com.threekidsinatrenchcoat.giftideaminder.data.model.Gift>) {
        suggestions[personId] = giftSuggestions
    }
    
    fun getSuggestions(personId: Int): List<com.threekidsinatrenchcoat.giftideaminder.data.model.Gift> {
        return suggestions.remove(personId) ?: emptyList() // Remove after getting to prevent stale data
    }
}

@HiltViewModel
class PersonFlowViewModel @Inject constructor(
    private val personRepo: PersonRepository,
    private val importantDateRepo: ImportantDateRepository,
    private val relRepo: RelationshipTypeRepository,
    private val smsAnalysisRepo: SmsAnalysisRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    enum class Step { Details, Dates, Preferences, Review }

    data class UiState(
        val isEditing: Boolean = false,
        val personId: Int? = null,
        val step: Step = Step.Details,
        val availableRelationships: List<String> = listOf("Spouse", "Partner", "Parent", "Child", "Sibling", "Friend", "Coworker"),
        val selectedRelationships: List<String> = emptyList(),
        val name: String = "",
        val preferences: List<String> = emptyList(),
        val datePrompts: List<String> = emptyList(),
        val pickedDates: Map<String, java.time.LocalDate> = emptyMap(),
        val relationshipTypes: Map<String, RelationshipType> = emptyMap(),
        // Labels for extra date rows the user added (can exist without a picked date)
        val additionalDateLabels: List<String> = emptyList(),
        // Labels the user chose to remove/hide from the list (e.g., hiding prompts)
        val removedDateLabels: Set<String> = emptySet(),
        // SMS scanning for gift insights
        val smsAnalysisEnabled: Boolean = false
    )

    data class NavResult(
        val navigateBack: Boolean = false, 
        val saved: Boolean = false, 
        val successMessage: String? = null,
        val navigateToSuggestions: Int? = null // personId to navigate to suggestions for
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { relRepo.ensureSeeded() }
        // Observe relationship types to populate available list
        viewModelScope.launch {
            relRepo.getAll().collect { types ->
                val names = types.map { it.name }
                val typeMap = types.associateBy { it.name }
                _uiState.update { it.copy(
                    availableRelationships = if (names.isEmpty()) it.availableRelationships else names,
                    relationshipTypes = typeMap
                ) }
            }
        }
        savedStateHandle.get<Int>("personId")?.let { id ->
            viewModelScope.launch {
                personRepo.getPersonByIdSuspend(id)?.let { person ->
                    _uiState.update {
                        it.copy(
                            isEditing = true,
                            personId = id,
                            selectedRelationships = person.relationships,
                            name = person.name,
                            preferences = person.preferences,
                            step = Step.Details
                        )
                    }
                    // Prefill dates for edit mode
                    importantDateRepo.getForPerson(id)
                        .debounce(300)
                        .distinctUntilChanged()
                        .collect { existing ->
                        val map = existing.associate { it.label to it.date }
                        _uiState.update { it.copy(pickedDates = map) }
                    }
                }
            }
        }
    }

    fun onRelationshipSelected(label: String) {
        _uiState.update { state ->
            val current = state.selectedRelationships
            val updated = if (label in current) {
                current - label
            } else {
                current + label
            }
            state.copy(selectedRelationships = updated)
        }
    }

    fun onAddNewRelationshipType(name: String, hasBirthday: Boolean = true, hasAnniversary: Boolean = false) {
        if (name.trim().isEmpty()) return
        val trimmedName = name.trim()
        viewModelScope.launch {
            val relationshipType = RelationshipType(
                name = trimmedName,
                hasBirthday = hasBirthday,
                hasAnniversary = hasAnniversary
            )
            relRepo.insert(relationshipType)
            
            // Automatically select the newly added relationship type
            _uiState.update { state ->
                state.copy(selectedRelationships = state.selectedRelationships + trimmedName)
            }
        }
    }

    fun onNameChange(new: String) {
        _uiState.update { it.copy(name = new) }
    }

    fun onAddPreference(item: String) {
        val trimmed = item.trim()
        if (trimmed.isEmpty()) return
        _uiState.update { s -> s.copy(preferences = s.preferences + trimmed) }
    }

    fun onRemovePreference(item: String) {
        _uiState.update { s -> s.copy(preferences = s.preferences - item) }
    }

    fun onBack(): NavResult {
        val current = _uiState.value
        val prev = when (current.step) {
            Step.Details -> return NavResult(navigateBack = true)
            Step.Dates -> Step.Details
            Step.Preferences -> Step.Dates
            Step.Review -> Step.Preferences
        }
        _uiState.update { it.copy(step = prev) }
        return NavResult()
    }

    fun onNextOrSave(): NavResult {
        val current = _uiState.value
        return when (current.step) {
            Step.Details -> {
                val relations = current.selectedRelationships
                // Only generate relationship-based date prompts for new person records
                // For existing records, don't add prompts - let user work with existing dates
                val prompts = if (current.isEditing) {
                    // When editing, don't add relationship-based prompts
                    // User already has their dates set up and can manually add more if needed
                    emptyList()
                } else {
                    // When creating new person, generate prompts based on relationships
                    buildSet<String> {
                        relations.forEach { relation ->
                            val rt = _uiState.value.relationshipTypes[relation]
                            if (rt != null) {
                                if (rt.hasBirthday) add("Birthday")
                                if (rt.hasAnniversary) add("Anniversary")
                            } else {
                                when (relation) {
                                    "Spouse", "Partner" -> {
                                        add("Birthday")
                                        add("Anniversary")
                                    }
                                    else -> add("Birthday")
                                }
                            }
                        }
                    }.toList()
                }
                _uiState.update { it.copy(step = Step.Dates, datePrompts = prompts) }
                NavResult()
            }
            Step.Dates -> {
                _uiState.update { it.copy(step = Step.Preferences) }
                NavResult()
            }
            Step.Preferences -> {
                _uiState.update { it.copy(step = Step.Review) }
                NavResult()
            }
            Step.Review -> {
                val shouldAnalyzeSms = current.smsAnalysisEnabled
                var finalPersonId: Int? = null
                
                viewModelScope.launch { 
                    finalPersonId = persistPersonAndDates()
                    if (shouldAnalyzeSms && finalPersonId != null) {
                        // Wait for SMS analysis to complete
                        val analysisResult = smsAnalysisRepo.analyzeSmsForPerson(finalPersonId!!) { personId ->
                            // Navigation will be handled by NavResult
                        }
                        
                        // Store suggestions in global companion object for pickup by PersonIdeasScreen
                        if (analysisResult is com.threekidsinatrenchcoat.giftideaminder.data.repository.SmsAnalysisResult.Success) {
                            SmsAnalysisCache.storeSuggestions(finalPersonId!!, analysisResult.generatedSuggestions)
                        }
                    }
                }
                
                val msg = if (_uiState.value.isEditing) {
                    if (shouldAnalyzeSms) "${_uiState.value.name} was updated! Analyzing messages for gift ideas..." 
                    else "${_uiState.value.name} was updated"
                } else {
                    if (shouldAnalyzeSms) "${_uiState.value.name} was added! Analyzing messages for gift ideas..." 
                    else "${_uiState.value.name} was added"
                }
                
                NavResult(
                    saved = true, 
                    successMessage = msg,
                    navigateToSuggestions = if (shouldAnalyzeSms) current.personId else null
                )
            }
        }
    }

    fun onDatePicked(label: String, date: java.time.LocalDate) {
        _uiState.update { it.copy(pickedDates = it.pickedDates + (label to date)) }
    }

    fun onRemoveDate(label: String) {
        _uiState.update { it.copy(pickedDates = it.pickedDates - label) }
    }

    fun onAddDateItem(typeOrLabel: String) {
        val trimmed = typeOrLabel.trim()
        if (trimmed.isEmpty()) return
        _uiState.update { state ->
            val unique = generateUniqueLabel(trimmed, state)
            val nextAdditional = if (unique in state.additionalDateLabels) state.additionalDateLabels else state.additionalDateLabels + unique
            state.copy(
                additionalDateLabels = nextAdditional,
                removedDateLabels = state.removedDateLabels - unique
            )
        }
    }

    fun onRemoveDateItem(label: String) {
        _uiState.update { state ->
            val newAdditional = state.additionalDateLabels - label
            val newPicked = state.pickedDates - label
            val newRemoved = if (label in state.datePrompts) state.removedDateLabels + label else state.removedDateLabels
            state.copy(
                additionalDateLabels = newAdditional,
                pickedDates = newPicked,
                removedDateLabels = newRemoved
            )
        }
    }

    fun onChangeDateLabel(oldLabel: String, newLabelRaw: String) {
        val proposed = newLabelRaw.trim()
        if (proposed.isEmpty() || proposed == oldLabel) return
        _uiState.update { state ->
            val unique = generateUniqueLabel(proposed, state, excluding = oldLabel)
            val newPicked = state.pickedDates.toMutableMap()
            state.pickedDates[oldLabel]?.let { date ->
                newPicked.remove(oldLabel)
                newPicked[unique] = date
            }
            val newAdditional = state.additionalDateLabels.map { if (it == oldLabel) unique else it }
            val newRemoved = if (oldLabel in state.removedDateLabels) state.removedDateLabels - oldLabel else state.removedDateLabels
            state.copy(pickedDates = newPicked, additionalDateLabels = newAdditional, removedDateLabels = newRemoved)
        }
    }

    fun onSmsAnalysisToggle(enabled: Boolean) {
        _uiState.update { it.copy(smsAnalysisEnabled = enabled) }
    }

    private fun generateUniqueLabel(base: String, state: UiState, excluding: String? = null): String {
        val occupied = (state.pickedDates.keys + state.additionalDateLabels + state.datePrompts)
            .filter { it != excluding }
            .toMutableSet()
        if (base !in occupied) return base
        var index = 2
        var candidate = "$base ($index)"
        while (candidate in occupied) {
            index += 1
            candidate = "$base ($index)"
        }
        return candidate
    }

    private suspend fun persistPersonAndDates(): Int? {
        val s = _uiState.value
        val person = Person(
            id = s.personId ?: 0,
            name = s.name,
            relationships = s.selectedRelationships,
            preferences = s.preferences
        )
        val finalPersonId = if (s.isEditing) {
            personRepo.update(person)
            person.id
        } else {
            personRepo.insert(person)
        }
        // Only save dates that actually have a date assigned 
        // Double-check: only save entries where both label and date are valid
        val dates = s.pickedDates
            .filter { (label, date) -> 
                label.isNotBlank() && 
                // Ensure we're not somehow saving very old dates that might be placeholders
                date.year > 1900
            }
            .map { (label, date) -> ImportantDate(personId = finalPersonId, label = label, date = date) }
        
        importantDateRepo.replaceForPerson(finalPersonId, dates)
        
        return finalPersonId
    }
}

 
