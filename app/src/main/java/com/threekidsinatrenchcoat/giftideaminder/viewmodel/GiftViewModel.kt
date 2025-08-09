package com.threekidsinatrenchcoat.giftideaminder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threekidsinatrenchcoat.giftideaminder.data.dao.GiftDao
import com.threekidsinatrenchcoat.giftideaminder.data.model.Gift
import com.threekidsinatrenchcoat.giftideaminder.data.model.GiftWithHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class GiftViewModel @Inject constructor(
    private val giftDao: GiftDao
) : ViewModel() {

    // ---------- Public DAO-backed streams ----------
    val allGifts: Flow<List<Gift>> = giftDao.getAllGifts()

    /** Returns a Flow of Gift entities. */
    fun getGiftById(id: Int): Flow<Gift> = giftDao.getGiftById(id)

    /** Returns a Flow of Gift with its price history. */
    fun getGiftWithHistoryById(id: Int): Flow<GiftWithHistory> = giftDao.getGiftWithHistoryById(id)

    // ---------- UI State (SSOT like AddEditRecipientViewModel) ----------
    data class GiftUiState(
        val id: Int? = null,
        val title: String = "",
        val description: String = "",
        val url: String = "",
        val priceText: String = "",
        val eventDateMillis: Long = -1L,
        val personId: Int? = null,
        val isSaving: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(GiftUiState())
    val uiState: StateFlow<GiftUiState> = _uiState.asStateFlow()

    // ---------- One-off UI events (snackbar/nav) ----------
    sealed interface GiftEvent { data object Saved : GiftEvent }
    private val _events = MutableSharedFlow<GiftEvent>()
    val events: SharedFlow<GiftEvent> = _events.asSharedFlow()

    // ---------- State updaters ----------
    fun loadForEdit(gift: Gift) {
        _uiState.update {
            it.copy(
                id = gift.id,
                title = gift.title,
                description = gift.description.orEmpty(),
                url = gift.url.orEmpty(),
                priceText = gift.currentPrice?.toString().orEmpty(),
                eventDateMillis = gift.eventDate ?: -1L,
                personId = gift.personId
            )
        }
    }

    fun onTitleChanged(v: String) = _uiState.update { it.copy(title = v) }
    fun onDescriptionChanged(v: String) = _uiState.update { it.copy(description = v) }
    fun onUrlChanged(v: String) = _uiState.update { it.copy(url = v) }
    fun onPriceChanged(v: String) = _uiState.update { it.copy(priceText = v) }
    fun onEventDateChanged(millis: Long) = _uiState.update { it.copy(eventDateMillis = millis) }
    fun onPersonSelected(id: Int?) = _uiState.update { it.copy(personId = id) }

    // ---------- Save (mirrors AddEditRecipientViewModel.onSave) ----------
    fun onSave() {
        val s = _uiState.value

        // Minimal example validation
        if (s.title.isBlank()) {
            _uiState.update { it.copy(error = "Title is required") }
            return
        }

        val gift = Gift(
            id = s.id ?: 0,
            title = s.title,
            description = s.description.ifBlank { null },
            url = s.url.ifBlank { null },
            currentPrice = s.priceText.toDoubleOrNull(),
            eventDate = if (s.eventDateMillis > 0) s.eventDateMillis else null,
            personId = s.personId
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            try {
                if (s.id != null) {
                    giftDao.update(gift)
                } else {
                    giftDao.insert(gift)
                }
                _events.emit(GiftEvent.Saved)
            } catch (t: Throwable) {
                _uiState.update { it.copy(error = t.message ?: "Save failed") }
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    // ---------- Legacy helpers kept for callers still using them ----------
    /** Direct insert for legacy callers. Prefer onSave() with state. */
    fun insertGift(gift: Gift) {
        viewModelScope.launch { giftDao.insert(gift) }
    }

    /** Direct update for legacy callers. Prefer onSave() with state. */
    fun updateGift(gift: Gift) {
        viewModelScope.launch { giftDao.update(gift) }
    }

    /** Direct delete convenience. */
    fun deleteGift(gift: Gift) {
        viewModelScope.launch { giftDao.delete(gift) }
    }

    // --- Stubs for legacy usages in screens ---
    fun updatePriceForGift(gift: Gift) {
        // Placeholder: no-op; implement price service integration later
    }

    private val _suggestions = MutableStateFlow<List<Gift>>(emptyList())
    val suggestions: StateFlow<List<Gift>> = _suggestions.asStateFlow()
    fun fetchSuggestions() { /* no-op placeholder; keep empty */ }
    fun dismissSuggestion(@Suppress("UNUSED_PARAMETER") suggestion: Gift) { /* no-op placeholder */ }

    // ---------- Transitional: maintain signature used by your screen ----------
    /** Transitional convenience — funnels into state + onSave(). */
    fun onSaveGift(
        title: String,
        description: String,
        url: String,
        price: String,
        eventDate: Long,
        selectedPersonId: Int?,
        existingGiftId: Int? = null
    ) {
        _uiState.update {
            it.copy(
                id = existingGiftId,
                title = title,
                description = description,
                url = url,
                priceText = price,
                eventDateMillis = eventDate,
                personId = selectedPersonId
            )
        }
        onSave()
    }
}
