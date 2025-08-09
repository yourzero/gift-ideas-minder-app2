package com.giftideaminder.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giftideaminder.data.model.Person
import com.giftideaminder.data.model.RelationshipType
import com.giftideaminder.data.repository.PersonRepository
import com.giftideaminder.data.repository.ImportantDateRepository
import com.giftideaminder.data.model.ImportantDate
import com.giftideaminder.data.repository.RelationshipTypeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonFlowViewModel @Inject constructor(
    private val personRepo: PersonRepository,
    private val importantDateRepo: ImportantDateRepository,
    private val relRepo: RelationshipTypeRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    enum class Step { Relationship, Details, Dates, Review }

    data class UiState(
        val isEditing: Boolean = false,
        val personId: Int? = null,
        val step: Step = Step.Relationship,
        val availableRelationships: List<String> = listOf("Spouse", "Partner", "Parent", "Child", "Sibling", "Friend", "Coworker"),
        val selectedRelationship: String? = null,
        val name: String = "",
        val datePrompts: List<String> = emptyList(),
        val pickedDates: Map<String, java.time.LocalDate> = emptyMap(),
        val relationshipTypes: Map<String, RelationshipType> = emptyMap()
    )

    data class NavResult(val navigateBack: Boolean = false, val saved: Boolean = false, val successMessage: String? = null)

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
                            selectedRelationship = person.relationships.firstOrNull(),
                            name = person.name,
                            step = Step.Relationship
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
        _uiState.update { it.copy(selectedRelationship = label) }
    }

    fun onNameChange(new: String) {
        _uiState.update { it.copy(name = new) }
    }

    fun onBack(): NavResult {
        val current = _uiState.value
        val prev = when (current.step) {
            Step.Relationship -> return NavResult(navigateBack = true)
            Step.Details -> Step.Relationship
            Step.Dates -> Step.Details
            Step.Review -> Step.Dates
        }
        _uiState.update { it.copy(step = prev) }
        return NavResult()
    }

    fun onNextOrSave(): NavResult {
        val current = _uiState.value
        return when (current.step) {
            Step.Relationship -> {
                val relation = current.selectedRelationship
                val prompts = if (relation == null) emptyList() else {
                    val rt = _uiState.value.relationshipTypes[relation]
                    if (rt != null) {
                        buildList {
                            if (rt.hasBirthday) add("Birthday")
                            if (rt.hasAnniversary) add("Anniversary")
                        }
                    } else {
                        when (relation) {
                            "Spouse", "Partner" -> listOf("Birthday", "Anniversary")
                            else -> listOf("Birthday")
                        }
                    }
                }
                _uiState.update { it.copy(step = Step.Details, datePrompts = prompts) }
                NavResult()
            }
            Step.Details -> {
                _uiState.update { it.copy(step = Step.Dates) }
                NavResult()
            }
            Step.Dates -> {
                _uiState.update { it.copy(step = Step.Review) }
                NavResult()
            }
            Step.Review -> {
                viewModelScope.launch { persistPersonAndDates() }
                val msg = if (_uiState.value.isEditing) "${_uiState.value.name} was updated" else "${_uiState.value.name} was added"
                NavResult(saved = true, successMessage = msg)
            }
        }
    }

    fun onDatePicked(label: String, date: java.time.LocalDate) {
        _uiState.update { it.copy(pickedDates = it.pickedDates + (label to date)) }
    }

    fun onRemoveDate(label: String) {
        _uiState.update { it.copy(pickedDates = it.pickedDates - label) }
    }

    private suspend fun persistPersonAndDates() {
        val s = _uiState.value
        val person = Person(
            id = s.personId ?: 0,
            name = s.name,
            relationships = listOfNotNull(s.selectedRelationship)
        )
        val finalPersonId = if (s.isEditing) {
            personRepo.update(person)
            person.id
        } else {
            personRepo.insert(person)
        }
        val dates = s.pickedDates.map { (label, date) -> ImportantDate(personId = finalPersonId, label = label, date = date) }
        importantDateRepo.replaceForPerson(finalPersonId, dates)
    }
}

 
