package com.threekidsinatrenchcoat.giftideaminder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threekidsinatrenchcoat.giftideaminder.BuildConfig
import com.threekidsinatrenchcoat.giftideaminder.data.dao.GiftDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.PersonDao
import com.threekidsinatrenchcoat.giftideaminder.data.model.Gift
import com.threekidsinatrenchcoat.giftideaminder.data.model.GiftWithHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@HiltViewModel
class GiftViewModel @Inject constructor(
    private val giftDao: GiftDao,
    private val aiRepo: com.threekidsinatrenchcoat.giftideaminder.data.repository.AISuggestionRepository,
    private val personDao: PersonDao
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
        val error: String? = null,
        // AddGiftFlowScreen specific state
        val stepIndex: Int = 0,
        val selectedPersonName: String? = null,
        val ideaText: String = ""
    )

    private val _uiState = MutableStateFlow(GiftUiState())
    val uiState: StateFlow<GiftUiState> = _uiState.asStateFlow()

    // ---------- One-off UI events (snackbar/nav) ----------
    sealed interface GiftEvent { data object Saved : GiftEvent }
    private val _events = MutableSharedFlow<GiftEvent>()
    val events: SharedFlow<GiftEvent> = _events.asSharedFlow()

    // ---------- State updaters ----------
    fun resetState() {
        _uiState.value = GiftUiState()
    }

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

    // ---------- AddGiftFlowScreen specific methods ----------
    fun onStepBack() = _uiState.update { 
        it.copy(stepIndex = (it.stepIndex - 1).coerceAtLeast(0)) 
    }
    
    fun onStepNext() = _uiState.update { 
        it.copy(stepIndex = (it.stepIndex + 1).coerceAtMost(2)) 
    }
    
    fun onIdeaTextChanged(text: String) = _uiState.update { 
        it.copy(ideaText = text, description = text) 
    }
    
    fun onDateSelectedFlow(millis: Long?) = _uiState.update { 
        it.copy(eventDateMillis = millis ?: -1L) 
    }
    
    fun onPersonSelectedFlow(personId: Int?, personName: String?) = _uiState.update { 
        it.copy(personId = personId, selectedPersonName = personName) 
    }
    
    fun onSelectPersonClick() {
        // This will be handled by the screen to show person selection dialog
        // The actual selection will be done via onPersonSelectedFlow
    }

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
    private val _isLoadingSuggestions = MutableStateFlow(false)
    val isLoadingSuggestions: StateFlow<Boolean> = _isLoadingSuggestions.asStateFlow()
    private val _suggestionsError = MutableStateFlow<String?>(null)
    val suggestionsError: StateFlow<String?> = _suggestionsError.asStateFlow()
    private var lastSuggestionsFetchMs: Long = 0L

    // Retry state
    private val _currentRetryCount = MutableStateFlow(0)
    val currentRetryCount: StateFlow<Int> = _currentRetryCount.asStateFlow()
    private val _isRetrying = MutableStateFlow(false)
    val isRetrying: StateFlow<Boolean> = _isRetrying.asStateFlow()

    // People lookup for UI labels on suggestions
    private val _peopleById: MutableStateFlow<Map<Int, String>> = MutableStateFlow(emptyMap())
    val peopleById: StateFlow<Map<Int, String>> = _peopleById.asStateFlow()

    // Debug and AI prompt state - using BuildConfig directly for simplicity
    private val _showDebugPrompts = MutableStateFlow(BuildConfig.DEBUG_AI_PROMPTS)
    val showDebugPrompts: StateFlow<Boolean> = _showDebugPrompts.asStateFlow()

    private val _currentAiPrompt = MutableStateFlow<String>("")
    val currentAiPrompt: StateFlow<String> = _currentAiPrompt.asStateFlow()

    init {
        viewModelScope.launch {
            personDao.getAllPersons().collect { people ->
                _peopleById.value = people.associate { it.id to it.name }
            }
        }

        // Debug prompts enabled via BuildConfig
    }

    fun fetchSuggestions() {
        viewModelScope.launch {
            if (!BuildConfig.AI_ENABLED) {
                _suggestions.value = emptyList()
                _suggestionsError.value = "AI disabled"
                return@launch
            }
            val now = System.currentTimeMillis()
            if (now - lastSuggestionsFetchMs < 10_000) {
                // Debounce fast refresh taps
                return@launch
            }
            lastSuggestionsFetchMs = now

            executeWithRetry(
                operation = {
                    // Set current prompt for debug display if enabled
                    _currentAiPrompt.value = "Fetching person-centric suggestions (top 3, 3 per person)"
                    aiRepo.fetchSuggestionsPersonCentric(topN = 3, perPerson = 3)
                },
                onSuccess = { ideas -> _suggestions.value = ideas }
            )
        }
    }

    private suspend fun <T> executeWithRetry(
        operation: suspend () -> T,
        onSuccess: (T) -> Unit,
        maxRetries: Int = BuildConfig.MAX_AI_RETRIES
    ) {
        _isLoadingSuggestions.value = true
        _suggestionsError.value = null
        _currentRetryCount.value = 0
        _isRetrying.value = false

        repeat(maxRetries + 1) { attempt ->
            try {
                val result = operation()
                onSuccess(result)
                return
            } catch (t: Throwable) {
                if (attempt < maxRetries) {
                    _currentRetryCount.value = attempt + 1
                    _isRetrying.value = true
                    // Exponential backoff: 1s, 2s, 4s
                    delay(1000L * (1 shl attempt))
                } else {
                    _suggestionsError.value = "Failed after $maxRetries retries: ${t.message ?: "Unknown error"}"
                }
            } finally {
                if (attempt == maxRetries) {
                    _isLoadingSuggestions.value = false
                    _isRetrying.value = false
                }
            }
        }
    }

    fun dismissSuggestion(suggestion: Gift) {
        viewModelScope.launch {
            try {
                aiRepo.dismissSuggestion(suggestion)
            } finally {
                _suggestions.update { list -> list.filterNot { it.title == suggestion.title && it.url == suggestion.url } }
            }
        }
    }

    fun fetchSuggestionsByBudget(min: Double, max: Double, personId: Int? = null) {
        viewModelScope.launch {
            if (!BuildConfig.AI_ENABLED) {
                _suggestions.value = emptyList()
                _suggestionsError.value = "AI disabled"
                return@launch
            }
            _isLoadingSuggestions.value = true
            _suggestionsError.value = null
            try {
                val ideas = aiRepo.fetchSuggestionsByBudget(min, max, personId)
                _suggestions.value = ideas
            } catch (t: Throwable) {
                _suggestionsError.value = t.message ?: "Failed to load budget ideas"
            } finally {
                _isLoadingSuggestions.value = false
            }
        }
    }

    fun fetchSuggestionsForPerson(personId: Int, perPerson: Int = 3) {
        viewModelScope.launch {
            if (!BuildConfig.AI_ENABLED) {
                _suggestions.value = emptyList()
                _suggestionsError.value = "AI disabled"
                return@launch
            }

            executeWithRetry(
                operation = {
                    // Set current prompt for debug display if enabled
                    _currentAiPrompt.value = "Fetching suggestions for person ID $personId ($perPerson suggestions)"
                    aiRepo.fetchSuggestionsForPerson(personId, perPerson)
                },
                onSuccess = { ideas -> _suggestions.value = ideas }
            )
        }
    }

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


    /** Reset transient UI state for creating a new gift. */
    fun resetForCreate() {
        _uiState.value = GiftUiState()
    }
    
    /** Reset specifically for AddGiftFlowScreen usage */
    fun resetForCreateFlow() {
        _uiState.value = GiftUiState(stepIndex = 0)
    }
}
