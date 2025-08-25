package com.threekidsinatrenchcoat.giftideaminder.core.ai

/**
 * Interface for AI-powered interest question generation
 */
interface InterestAiQuestioner {
    /**
     * Generate AI-powered questions for exploring interests in a specific category
     * @param category The interest category to generate questions for
     * @return List of thoughtful questions to help users discover more interests
     */
    suspend fun generateQuestions(category: String): List<String>
}