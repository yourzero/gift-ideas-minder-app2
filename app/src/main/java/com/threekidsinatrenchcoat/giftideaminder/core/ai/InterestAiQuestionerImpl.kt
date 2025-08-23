package com.threekidsinatrenchcoat.giftideaminder.core.ai

import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stub implementation of InterestAiQuestioner
 * Returns canned questions for development purposes
 */
@Singleton
class InterestAiQuestionerImpl @Inject constructor() : InterestAiQuestioner {
    
    private val questionMap = mapOf(
        "Sports" to listOf(
            "What's your favorite sport to play or watch?",
            "Do you prefer team sports or individual activities?",
            "Have you tried any extreme sports?",
            "What's your fitness routine like?",
            "Do you follow any sports teams?"
        ),
        "Music" to listOf(
            "What genres of music do you enjoy most?",
            "Do you play any instruments?",
            "Have you been to any memorable concerts?",
            "Do you prefer live music or recordings?",
            "What's your favorite way to discover new music?"
        ),
        "Technology" to listOf(
            "Are you interested in the latest gadgets?",
            "Do you enjoy coding or programming?",
            "What's your favorite app or software?",
            "Are you into gaming? What platforms?",
            "How do you stay updated with tech trends?"
        ),
        "Arts" to listOf(
            "Do you create any art yourself?",
            "What art mediums interest you most?",
            "Do you visit museums or galleries?",
            "Are you into photography?",
            "Do you enjoy crafting or DIY projects?"
        ),
        "Food" to listOf(
            "Do you enjoy cooking or baking?",
            "What's your favorite cuisine?",
            "Are you adventurous with trying new foods?",
            "Do you have any dietary preferences?",
            "What's your favorite restaurant or food spot?"
        )
    )
    
    override suspend fun generateQuestions(category: String): List<String> {
        // Simulate API delay
        delay(700)
        
        // Return questions for the category, or default questions
        return questionMap[category] ?: listOf(
            "What draws you to this topic?",
            "How did you first get interested in this?",
            "What aspects do you find most exciting?",
            "Do you have any favorites in this category?",
            "How often do you engage with this interest?"
        )
    }
}