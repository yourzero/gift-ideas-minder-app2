package com.threekidsinatrenchcoat.giftideaminder.core.ai

import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stub implementation of InterestAiSuggester
 * Returns canned responses for development purposes
 */
@Singleton
class InterestAiSuggesterImpl @Inject constructor() : InterestAiSuggester {
    
    private val suggestionMap = mapOf(
        "Sports" to listOf("Basketball", "Soccer", "Tennis", "Swimming", "Running", "Cycling"),
        "Music" to listOf("Guitar", "Piano", "Drums", "Singing", "Jazz", "Rock"),
        "Technology" to listOf("Programming", "Gaming", "Gadgets", "AI", "Robotics", "Apps"),
        "Arts" to listOf("Painting", "Drawing", "Photography", "Sculpture", "Design", "Crafts"),
        "Food" to listOf("Cooking", "Baking", "Coffee", "Wine", "Restaurants", "Recipes"),
        "Travel" to listOf("Adventure", "Culture", "History", "Nature", "Cities", "Photography"),
        "Books" to listOf("Fiction", "Mystery", "Science", "Biography", "Fantasy", "Romance"),
        "Movies" to listOf("Action", "Comedy", "Drama", "Horror", "Sci-Fi", "Documentary")
    )
    
    override suspend fun getSuggestions(parentLabel: String): List<String> {
        // Simulate API delay
        delay(500)
        
        // Return suggestions for the parent label, or default suggestions
        return suggestionMap[parentLabel] ?: listOf(
            "Hobby 1",
            "Hobby 2", 
            "Activity 1",
            "Interest 1",
            "Passion 1"
        )
    }
}