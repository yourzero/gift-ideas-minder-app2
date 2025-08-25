package com.threekidsinatrenchcoat.giftideaminder.core.ai

import com.threekidsinatrenchcoat.giftideaminder.core.analytics.Analytics
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AI-powered question generation for the 20 Questions interest discovery flow.
 * Provides contextual questions based on category and previous answers.
 */
@Singleton
class InterestAiQuestioner @Inject constructor(
    private val analytics: Analytics
) {
    
    data class Question(
        val id: String,
        val text: String,
        val category: String,
        val followUpOnYes: List<String> = emptyList(),
        val followUpOnNo: List<String> = emptyList()
    )
    
    /**
     * Get questions for a specific category.
     * Currently uses static mapping but can be enhanced with dynamic AI generation.
     */
    fun getQuestionsForCategory(category: String): List<Question> {
        analytics.logEvent("questions_generated", mapOf("category" to category))
        
        return when (category.lowercase()) {
            "outdoors" -> listOf(
                Question("outdoors_1", "Do you enjoy hiking or walking in nature?", category,
                    followUpOnYes = listOf("Trail gear", "Hiking boots", "Day packs")),
                Question("outdoors_2", "Are you interested in camping?", category,
                    followUpOnYes = listOf("Camping equipment", "Portable stoves", "Sleeping bags")),
                Question("outdoors_3", "Do you like water activities?", category,
                    followUpOnYes = listOf("Water sports gear", "Beach accessories", "Kayaking equipment"))
            )
            
            "gaming" -> listOf(
                Question("gaming_1", "Do you prefer PC gaming over console gaming?", category,
                    followUpOnYes = listOf("Gaming keyboards", "Gaming mice", "Monitor upgrades")),
                Question("gaming_2", "Are you into competitive gaming or esports?", category,
                    followUpOnYes = listOf("Gaming headsets", "Pro controllers", "Gaming chairs")),
                Question("gaming_3", "Do you enjoy collecting gaming merchandise?", category,
                    followUpOnYes = listOf("Collectible figures", "Art books", "Limited editions"))
            )
            
            "technology" -> listOf(
                Question("tech_1", "Are you always looking for the latest gadgets?", category,
                    followUpOnYes = listOf("Smart home devices", "Wearable tech", "Latest accessories")),
                Question("tech_2", "Do you work from home and need productivity tools?", category,
                    followUpOnYes = listOf("Laptop stands", "USB-C hubs", "Wireless chargers")),
                Question("tech_3", "Are you interested in audio equipment?", category,
                    followUpOnYes = listOf("High-quality headphones", "Bluetooth speakers", "Audio interfaces"))
            )
            
            "cooking" -> listOf(
                Question("cooking_1", "Do you enjoy experimenting with new recipes?", category,
                    followUpOnYes = listOf("Specialty cookbooks", "Unique spices", "Kitchen gadgets")),
                Question("cooking_2", "Are you interested in professional-quality kitchen tools?", category,
                    followUpOnYes = listOf("Chef knives", "Cast iron cookware", "Professional equipment")),
                Question("cooking_3", "Do you like hosting dinner parties?", category,
                    followUpOnYes = listOf("Serving platters", "Wine accessories", "Table settings"))
            )
            
            "fitness" -> listOf(
                Question("fitness_1", "Do you prefer working out at home?", category,
                    followUpOnYes = listOf("Home gym equipment", "Resistance bands", "Yoga mats")),
                Question("fitness_2", "Are you training for specific fitness goals?", category,
                    followUpOnYes = listOf("Performance gear", "Fitness trackers", "Recovery tools")),
                Question("fitness_3", "Do you enjoy outdoor fitness activities?", category,
                    followUpOnYes = listOf("Running shoes", "Athletic wear", "Outdoor equipment"))
            )
            
            "reading" -> listOf(
                Question("reading_1", "Do you prefer physical books over digital?", category,
                    followUpOnYes = listOf("Book accessories", "Reading lights", "Bookends")),
                Question("reading_2", "Are you interested in specific genres?", category,
                    followUpOnYes = listOf("Genre-specific books", "Author collections", "Series sets")),
                Question("reading_3", "Do you like to track your reading progress?", category,
                    followUpOnYes = listOf("Reading journals", "Book tracking apps", "Literary planners"))
            )
            
            "music" -> listOf(
                Question("music_1", "Do you play any musical instruments?", category,
                    followUpOnYes = listOf("Instrument accessories", "Sheet music", "Practice tools")),
                Question("music_2", "Are you into high-quality audio equipment?", category,
                    followUpOnYes = listOf("Premium headphones", "Audio systems", "DACs and amps")),
                Question("music_3", "Do you collect physical music media?", category,
                    followUpOnYes = listOf("Vinyl records", "CD collections", "Limited editions"))
            )
            
            "art" -> listOf(
                Question("art_1", "Do you create digital art or traditional art?", category,
                    followUpOnYes = listOf("Digital tablets", "Traditional art supplies", "Software subscriptions")),
                Question("art_2", "Are you interested in learning new artistic techniques?", category,
                    followUpOnYes = listOf("Art classes", "Tutorial subscriptions", "Skill-building books")),
                Question("art_3", "Do you need better organization for your art supplies?", category,
                    followUpOnYes = listOf("Storage solutions", "Organizing systems", "Portfolio cases"))
            )
            
            "travel" -> listOf(
                Question("travel_1", "Do you travel frequently for business or pleasure?", category,
                    followUpOnYes = listOf("Travel accessories", "Luggage upgrades", "Comfort items")),
                Question("travel_2", "Are you interested in adventure travel?", category,
                    followUpOnYes = listOf("Adventure gear", "Travel equipment", "Safety accessories")),
                Question("travel_3", "Do you like to document your travels?", category,
                    followUpOnYes = listOf("Travel journals", "Camera equipment", "Photo accessories"))
            )
            
            "pets" -> listOf(
                Question("pets_1", "Do you have a dog, cat, or other pet?", category,
                    followUpOnYes = listOf("Pet-specific accessories", "Comfort items", "Health products")),
                Question("pets_2", "Are you interested in training or behavioral tools?", category,
                    followUpOnYes = listOf("Training accessories", "Interactive toys", "Behavioral aids")),
                Question("pets_3", "Do you like to spoil your pets with luxury items?", category,
                    followUpOnYes = listOf("Premium pet products", "Luxury accessories", "High-end food"))
            )
            
            else -> listOf(
                Question("general_1", "Are you interested in learning new skills?", category,
                    followUpOnYes = listOf("Educational resources", "Skill-building tools", "Learning materials")),
                Question("general_2", "Do you enjoy luxury or premium versions of everyday items?", category,
                    followUpOnYes = listOf("Premium accessories", "Luxury upgrades", "High-quality basics")),
                Question("general_3", "Are you interested in unique or personalized items?", category,
                    followUpOnYes = listOf("Custom products", "Personalized accessories", "Unique finds"))
            )
        }
    }
    
    /**
     * Get follow-up interests based on question answers.
     */
    fun generateInterestsFromAnswers(
        category: String,
        answeredQuestions: List<Pair<Question, Boolean>>
    ): List<String> {
        val interests = mutableSetOf<String>()
        
        answeredQuestions.forEach { (question, answer) ->
            val followUps = if (answer) question.followUpOnYes else question.followUpOnNo
            interests.addAll(followUps)
        }
        
        analytics.logEvent("interests_generated_from_answers", mapOf(
            "category" to category,
            "questions_answered" to answeredQuestions.size,
            "interests_generated" to interests.size
        ))
        
        return interests.toList()
    }
}