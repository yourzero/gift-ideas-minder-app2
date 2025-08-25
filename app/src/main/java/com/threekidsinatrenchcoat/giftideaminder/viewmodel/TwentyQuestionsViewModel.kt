package com.threekidsinatrenchcoat.giftideaminder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threekidsinatrenchcoat.giftideaminder.data.model.InterestEntity
import com.threekidsinatrenchcoat.giftideaminder.data.repository.InterestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuestionCategory(
    val id: String,
    val name: String,
    val emoji: String,
    val description: String
)

data class CategoryQuestion(
    val question: String,
    val options: List<String>
)

data class QuestionAnswer(
    val categoryId: String,
    val question: String,
    val answer: String
)

@HiltViewModel
class TwentyQuestionsViewModel @Inject constructor(
    private val interestRepository: InterestRepository
) : ViewModel() {
    
    private val _personId = MutableStateFlow<Long?>(null)
    val personId: StateFlow<Long?> = _personId.asStateFlow()
    
    private val _currentStep = MutableStateFlow(0) // 0: category selection, 1: questions, 2: review
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()
    
    private val _selectedCategories = MutableStateFlow<List<QuestionCategory>>(emptyList())
    val selectedCategories: StateFlow<List<QuestionCategory>> = _selectedCategories.asStateFlow()
    
    private val _currentCategoryIndex = MutableStateFlow(0)
    val currentCategoryIndex: StateFlow<Int> = _currentCategoryIndex.asStateFlow()
    
    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()
    
    private val _answers = MutableStateFlow<List<QuestionAnswer>>(emptyList())
    val answers: StateFlow<List<QuestionAnswer>> = _answers.asStateFlow()
    
    private val _isComplete = MutableStateFlow(false)
    val isComplete: StateFlow<Boolean> = _isComplete.asStateFlow()
    
    // Static categories list
    val availableCategories = listOf(
        QuestionCategory("outdoors", "Outdoors", "🌲", "Nature, hiking, camping, sports"),
        QuestionCategory("gaming", "Video Games", "🎮", "Gaming, esports, streaming"),
        QuestionCategory("technology", "Technology", "💻", "Gadgets, software, innovation"),
        QuestionCategory("sports", "Sports", "⚽", "Physical activities, team sports"),
        QuestionCategory("arts", "Arts & Crafts", "🎨", "Creative projects, DIY"),
        QuestionCategory("music", "Music", "🎵", "Instruments, concerts, genres"),
        QuestionCategory("books", "Books & Reading", "📚", "Literature, learning, knowledge"),
        QuestionCategory("cooking", "Cooking", "🍳", "Food, recipes, culinary arts"),
        QuestionCategory("travel", "Travel", "✈️", "Places, cultures, adventures"),
        QuestionCategory("fitness", "Fitness", "💪", "Exercise, health, wellness")
    )
    
    fun setPersonId(personId: Long) {
        _personId.value = personId
    }
    
    fun selectCategories(categories: List<QuestionCategory>) {
        _selectedCategories.value = categories
        _currentStep.value = 1
    }
    
    fun getCurrentCategory(): QuestionCategory? {
        val categories = _selectedCategories.value
        val index = _currentCategoryIndex.value
        return categories.getOrNull(index)
    }
    
    fun getCurrentQuestions(): List<CategoryQuestion> {
        val category = getCurrentCategory() ?: return emptyList()
        return getQuestionsForCategory(category.id)
    }
    
    fun answerQuestion(answer: String) {
        val category = getCurrentCategory() ?: return
        val questions = getCurrentQuestions()
        val question = questions.getOrNull(_currentQuestionIndex.value) ?: return
        
        val questionAnswer = QuestionAnswer(
            categoryId = category.id,
            question = question.question,
            answer = answer
        )
        
        _answers.value = _answers.value + questionAnswer
        
        // Move to next question or category
        val nextQuestionIndex = _currentQuestionIndex.value + 1
        if (nextQuestionIndex < questions.size) {
            _currentQuestionIndex.value = nextQuestionIndex
        } else {
            // Move to next category
            val nextCategoryIndex = _currentCategoryIndex.value + 1
            if (nextCategoryIndex < _selectedCategories.value.size) {
                _currentCategoryIndex.value = nextCategoryIndex
                _currentQuestionIndex.value = 0
            } else {
                // All questions completed
                _currentStep.value = 2
            }
        }
    }
    
    fun skipCurrentQuestion() {
        answerQuestion("Skipped")
    }
    
    fun goToReview() {
        _currentStep.value = 2
    }
    
    fun finishFlow() {
        viewModelScope.launch {
            val personId = _personId.value ?: return@launch
            
            // Generate interests from answers
            val generatedInterests = generateInterestsFromAnswers(_answers.value)
            
            // Save interests to database
            generatedInterests.forEach { interest ->
                interestRepository.insertInterestEntity(
                    InterestEntity(
                        personId = personId,
                        name = interest.name,
                        description = interest.description
                    )
                )
            }
            
            _isComplete.value = true
        }
    }
    
    fun resetFlow() {
        _currentStep.value = 0
        _selectedCategories.value = emptyList()
        _currentCategoryIndex.value = 0
        _currentQuestionIndex.value = 0
        _answers.value = emptyList()
        _isComplete.value = false
    }
    
    fun getQuestionsForCategory(categoryId: String): List<CategoryQuestion> {
        return when (categoryId) {
            "outdoors" -> listOf(
                CategoryQuestion("What outdoor activities appeal to you?", listOf("Hiking", "Camping", "Rock climbing", "Photography", "Bird watching")),
                CategoryQuestion("What's your preferred outdoor setting?", listOf("Mountains", "Beaches", "Forests", "Deserts", "Lakes")),
                CategoryQuestion("How active do you like to be outdoors?", listOf("High intensity", "Moderate activity", "Relaxed pace", "Mixed levels"))
            )
            "gaming" -> listOf(
                CategoryQuestion("What gaming platforms do you prefer?", listOf("PC", "Console", "Mobile", "Retro/Classic", "Board games")),
                CategoryQuestion("What game genres interest you?", listOf("Action/Adventure", "Strategy", "RPG", "Puzzle", "Simulation")),
                CategoryQuestion("How do you prefer to game?", listOf("Solo play", "Local multiplayer", "Online multiplayer", "Competitive", "Casual"))
            )
            "technology" -> listOf(
                CategoryQuestion("What tech areas interest you?", listOf("Smartphones", "Computers", "Smart home", "AI/ML", "Web development")),
                CategoryQuestion("How do you engage with technology?", listOf("Early adopter", "Practical user", "Creator/Builder", "Learner", "Problem solver")),
                CategoryQuestion("What's your tech expertise level?", listOf("Beginner", "Intermediate", "Advanced", "Professional", "Mixed"))
            )
            "sports" -> listOf(
                CategoryQuestion("What types of sports interest you?", listOf("Team sports", "Individual sports", "Water sports", "Winter sports", "Combat sports")),
                CategoryQuestion("How do you prefer to engage with sports?", listOf("Playing actively", "Watching/Following", "Coaching/Teaching", "Fitness focus", "Social aspect")),
                CategoryQuestion("What's your activity level?", listOf("Professional/Competitive", "Regular participant", "Occasional player", "Spectator mainly", "Just starting"))
            )
            "arts" -> listOf(
                CategoryQuestion("What art forms appeal to you?", listOf("Visual arts", "Performing arts", "Crafts/DIY", "Digital art", "Writing")),
                CategoryQuestion("How do you engage with arts?", listOf("Creating original work", "Learning techniques", "Collecting/Appreciating", "Teaching others", "Collaborative projects")),
                CategoryQuestion("What's your skill level?", listOf("Professional", "Advanced amateur", "Intermediate", "Beginner", "Just exploring"))
            )
            "music" -> listOf(
                CategoryQuestion("How do you engage with music?", listOf("Playing instruments", "Listening/Discovering", "Creating/Composing", "Live events", "Music production")),
                CategoryQuestion("What genres appeal to you?", listOf("Rock/Pop", "Classical", "Jazz/Blues", "Electronic", "Folk/Acoustic")),
                CategoryQuestion("What's your musical background?", listOf("Professional musician", "Skilled amateur", "Learning", "Enthusiastic listener", "Casual interest"))
            )
            "books" -> listOf(
                CategoryQuestion("What genres do you enjoy?", listOf("Fiction", "Non-fiction", "Science fiction", "Mystery/Thriller", "Educational")),
                CategoryQuestion("How do you prefer to read?", listOf("Physical books", "E-books", "Audiobooks", "Mixed formats", "Short articles")),
                CategoryQuestion("What draws you to reading?", listOf("Entertainment", "Learning", "Escape", "Professional development", "Social discussion"))
            )
            "cooking" -> listOf(
                CategoryQuestion("What cooking styles interest you?", listOf("Home cooking", "Baking/Pastry", "International cuisine", "Healthy/Special diets", "Gourmet/Fine dining")),
                CategoryQuestion("How do you approach cooking?", listOf("From scratch", "Quick meals", "Experimenting", "Following recipes", "Meal planning")),
                CategoryQuestion("What's your skill level?", listOf("Professional chef", "Advanced home cook", "Competent cook", "Learning basics", "Just starting"))
            )
            "travel" -> listOf(
                CategoryQuestion("What types of travel appeal to you?", listOf("Adventure travel", "Cultural exploration", "Relaxation/Beach", "City breaks", "Nature/Wildlife")),
                CategoryQuestion("How do you prefer to travel?", listOf("Solo travel", "With family", "Group tours", "Road trips", "International")),
                CategoryQuestion("What draws you to travel?", listOf("New experiences", "Photography", "Food/Cuisine", "History/Culture", "Adventure/Challenge"))
            )
            "fitness" -> listOf(
                CategoryQuestion("What fitness activities interest you?", listOf("Gym/Weight training", "Cardio/Running", "Yoga/Pilates", "Group classes", "Outdoor fitness")),
                CategoryQuestion("What are your fitness goals?", listOf("Weight loss", "Muscle building", "General health", "Athletic performance", "Stress relief")),
                CategoryQuestion("How do you prefer to exercise?", listOf("Solo workouts", "Group activities", "Personal training", "Home workouts", "Varied routine"))
            )
            else -> emptyList()
        }
    }
    
    private fun generateInterestsFromAnswers(answers: List<QuestionAnswer>): List<InterestEntity> {
        val interests = mutableListOf<InterestEntity>()
        val answersGrouped = answers.groupBy { it.categoryId }
        
        answersGrouped.forEach { (categoryId, categoryAnswers) ->
            val category = availableCategories.find { it.id == categoryId } ?: return@forEach
            
            // Create parent interest for category
            val parentInterest = InterestEntity(
                personId = 0, // Will be set when saved
                name = category.name,
                description = "Generated from 20 Questions: ${categoryAnswers.map { it.answer }.filter { it != "Skipped" }.joinToString(", ")}"
            )
            
            interests.add(parentInterest)
        }
        
        return interests
    }
}