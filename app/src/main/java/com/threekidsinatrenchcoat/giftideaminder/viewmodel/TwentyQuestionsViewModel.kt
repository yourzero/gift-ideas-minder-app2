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
class TwentyQuestionsViewModel @Inject constructor(
    private val interestRepository: InterestRepository
) : ViewModel() {

    // ---------- UI State ----------
    
    data class TwentyQuestionsUiState(
        val availableCategories: List<String> = CATEGORIES,
        val currentCategory: String? = null,
        val currentQuestionIndex: Int = 0,
        val questions: List<String> = emptyList(),
        val answers: Map<String, List<String>> = emptyMap(),
        val isFlowComplete: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(TwentyQuestionsUiState())
    val uiState: StateFlow<TwentyQuestionsUiState> = _uiState.asStateFlow()

    // ---------- Flow Control Methods ----------
    
    /**
     * Select a category to start questions for
     */
    fun selectCategory(category: String) {
        if (!CATEGORIES.contains(category)) {
            _uiState.update { it.copy(error = "Invalid category selected") }
            return
        }

        val questions = CATEGORY_QUESTIONS[category] ?: emptyList()
        _uiState.update { state ->
            state.copy(
                currentCategory = category,
                currentQuestionIndex = 0,
                questions = questions,
                error = null
            )
        }
    }

    /**
     * Answer the current question with the provided response
     */
    fun answerQuestion(answer: String) {
        val currentState = _uiState.value
        val category = currentState.currentCategory ?: return
        val questionIndex = currentState.currentQuestionIndex

        if (questionIndex >= currentState.questions.size) return

        // Store the answer
        val categoryAnswers = currentState.answers[category]?.toMutableList() ?: mutableListOf()
        
        // Ensure the list is large enough for this question index
        while (categoryAnswers.size <= questionIndex) {
            categoryAnswers.add("")
        }
        categoryAnswers[questionIndex] = answer.trim()

        val updatedAnswers = currentState.answers.toMutableMap()
        updatedAnswers[category] = categoryAnswers

        _uiState.update { state ->
            state.copy(answers = updatedAnswers, error = null)
        }
    }

    /**
     * Skip the current question
     */
    fun skipQuestion() {
        answerQuestion("") // Empty answer means skipped
    }

    /**
     * Move to the next question
     */
    fun nextQuestion() {
        val currentState = _uiState.value
        if (currentState.currentQuestionIndex < currentState.questions.size - 1) {
            _uiState.update { state ->
                state.copy(currentQuestionIndex = state.currentQuestionIndex + 1)
            }
        }
    }

    /**
     * Move to the previous question
     */
    fun previousQuestion() {
        val currentState = _uiState.value
        if (currentState.currentQuestionIndex > 0) {
            _uiState.update { state ->
                state.copy(currentQuestionIndex = state.currentQuestionIndex - 1)
            }
        }
    }

    /**
     * Finish the flow and save interests for the person
     */
    fun finishFlow(personId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                saveInterestsFromAnswers(personId)
                
                _uiState.update { 
                    it.copy(
                        isFlowComplete = true,
                        isLoading = false
                    )
                }
            } catch (t: Throwable) {
                _uiState.update { 
                    it.copy(
                        error = t.message ?: "Failed to save interests",
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * Reset the flow to start over
     */
    fun resetFlow() {
        _uiState.update { 
            TwentyQuestionsUiState() 
        }
    }

    /**
     * Clear any error messages
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    /**
     * Check if the current question has been answered
     */
    fun isCurrentQuestionAnswered(): Boolean {
        val state = _uiState.value
        val category = state.currentCategory ?: return false
        val questionIndex = state.currentQuestionIndex
        val categoryAnswers = state.answers[category]
        
        return categoryAnswers != null && 
               questionIndex < categoryAnswers.size && 
               categoryAnswers[questionIndex].isNotBlank()
    }
    
    /**
     * Get the current question text
     */
    fun getCurrentQuestion(): String? {
        val state = _uiState.value
        return if (state.currentQuestionIndex < state.questions.size) {
            state.questions[state.currentQuestionIndex]
        } else null
    }
    
    /**
     * Get the current answer for the current question
     */
    fun getCurrentAnswer(): String {
        val state = _uiState.value
        val category = state.currentCategory ?: return ""
        val questionIndex = state.currentQuestionIndex
        val categoryAnswers = state.answers[category]
        
        return if (categoryAnswers != null && questionIndex < categoryAnswers.size) {
            categoryAnswers[questionIndex]
        } else ""
    }
    
    /**
     * Get progress information for the current category
     */
    fun getCategoryProgress(): Pair<Int, Int> {
        val state = _uiState.value
        return Pair(state.currentQuestionIndex + 1, state.questions.size)
    }
    
    /**
     * Check if we're on the last question of the current category
     */
    fun isLastQuestion(): Boolean {
        val state = _uiState.value
        return state.currentQuestionIndex >= state.questions.size - 1
    }
    
    /**
     * Check if we're on the first question of the current category
     */
    fun isFirstQuestion(): Boolean {
        val state = _uiState.value
        return state.currentQuestionIndex <= 0
    }

    // ---------- Helper Methods ----------
    
    /**
     * Save interests from collected answers, properly handling parent-child relationships
     */
    private suspend fun saveInterestsFromAnswers(personId: Int) {
        _uiState.value.answers.forEach { (category, answers) ->
            // Create parent interest for the category if there are any non-empty answers
            val nonEmptyAnswers = answers.filter { it.isNotBlank() }
            if (nonEmptyAnswers.isNotEmpty()) {
                // Insert parent interest first and get its ID
                val parentInterest = InterestEntity(
                    personId = personId,
                    parentId = null,
                    label = category
                )
                interestRepository.insertInterestEntity(parentInterest)
                
                // Since we can't easily get the inserted parent ID, we'll save the answers
                // as separate parent interests for now. In a more sophisticated implementation,
                // we would use the DAO to return the inserted ID or query for it.
                nonEmptyAnswers.forEach { answer ->
                    val answerInterest = InterestEntity(
                        personId = personId,
                        parentId = null, // Making these parent interests for simplicity
                        label = "$category: $answer"
                    )
                    interestRepository.insertInterestEntity(answerInterest)
                }
            }
        }
    }

    // ---------- Companion Object ----------
    
    companion object {
        /**
         * Static list of interest categories
         */
        val CATEGORIES = listOf(
            "Outdoors",
            "Video Games", 
            "Technology",
            "Sports",
            "Music",
            "Books",
            "Cooking",
            "Travel",
            "Art & Crafts",
            "Fitness"
        )

        /**
         * Map categories to their respective questions
         */
        val CATEGORY_QUESTIONS = mapOf(
            "Outdoors" to listOf(
                "Do you enjoy hiking or walking in nature?",
                "Are you interested in camping or outdoor adventures?",
                "Do you like water sports or beach activities?"
            ),
            "Video Games" to listOf(
                "Do you enjoy playing video games?",
                "Are you interested in specific gaming platforms or genres?",
                "Do you like multiplayer or competitive gaming?"
            ),
            "Technology" to listOf(
                "Are you into smartphones and mobile apps?",
                "Do you enjoy computer technology or gadgets?",
                "Are you interested in smart home devices or automation?"
            ),
            "Sports" to listOf(
                "Do you follow any particular sports or teams?",
                "Are you interested in playing sports yourself?",
                "Do you enjoy watching sports events or games?"
            ),
            "Music" to listOf(
                "What genres of music do you enjoy?",
                "Do you play any musical instruments?",
                "Are you interested in concerts or live music events?"
            ),
            "Books" to listOf(
                "What types of books do you like to read?",
                "Do you prefer fiction or non-fiction?",
                "Are you interested in audiobooks or e-books?"
            ),
            "Cooking" to listOf(
                "Do you enjoy cooking or trying new recipes?",
                "Are you interested in specific cuisines or cooking styles?",
                "Do you like kitchen gadgets or cooking equipment?"
            ),
            "Travel" to listOf(
                "What types of destinations do you prefer?",
                "Do you enjoy adventure travel or relaxing vacations?",
                "Are you interested in international or domestic travel?"
            ),
            "Art & Crafts" to listOf(
                "Do you enjoy creating art or crafts?",
                "Are you interested in specific art mediums or techniques?",
                "Do you like DIY projects or handmade items?"
            ),
            "Fitness" to listOf(
                "Do you enjoy working out or staying active?",
                "Are you interested in specific types of exercise or sports?",
                "Do you like fitness equipment or workout gear?"
            )
        )
    }
}