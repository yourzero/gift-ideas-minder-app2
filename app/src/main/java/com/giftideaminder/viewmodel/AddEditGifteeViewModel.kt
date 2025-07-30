package com.giftideaminder.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giftideaminder.data.model.Person
import com.giftideaminder.data.repository.PersonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GifteeUiState(
    val isEditing: Boolean = false,
    val id: Int? = null,
    val photoUri: Uri? = null,
    val name: String = "",
    val eventDate: Long? = null,
    val isDatePickerOpen: Boolean = false,
    val relationships: List<String> = emptyList(),
    val isRelationshipDropdownOpen: Boolean = false,
    val notes: String = ""
)

@HiltViewModel
class AddEditGifteeViewModel @Inject constructor(
    private val personRepo: PersonRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(GifteeUiState())
    val uiState: StateFlow<GifteeUiState> = _uiState.asStateFlow()

    // Expose relationship options publicly
    val relationshipOptions: List<String> = listOf("Family", "Friend", "Coworker")

    init {
        savedStateHandle.get<Int>("gifteeId")?.let { id ->
            viewModelScope.launch {
                personRepo.getPersonById(id).firstOrNull()?.let { person ->
                    _uiState.update { s ->
                        s.copy(
                            isEditing = true,
                            id = person.id,
                            photoUri = person.photoUri?.let(Uri::parse),
                            name = person.name,
                            eventDate = person.birthday,
                            relationships = person.relationships,
                            notes = person.notes ?: ""
                        )
                    }
                }
            }
        }
    }

    fun onNameChange(new: String) = _uiState.update { it.copy(name = new) }
    fun onEventDateChange(ts: Long) = _uiState.update { it.copy(eventDate = ts) }
    fun onNotesChange(new: String) = _uiState.update { it.copy(notes = new) }
    fun onShowDatePicker(open: Boolean) =
        _uiState.update { it.copy(isDatePickerOpen = open) }
    fun onShowRelationshipDropdown(open: Boolean) =
        _uiState.update { it.copy(isRelationshipDropdownOpen = open) }
    fun onRelationshipsChange(new: List<String>) =
        _uiState.update { it.copy(relationships = new) }

    fun findContactByName(ctx: Context, name: String) {
        viewModelScope.launch {
            // Implement actual lookup...
        }
    }

    fun loadContact(ctx: Context, contactUri: Uri) {
        viewModelScope.launch {
            // Implementation...
        }
    }

    fun onSave() {
        viewModelScope.launch {
            val s = _uiState.value
            // Save Person entity
        }
    }
}
