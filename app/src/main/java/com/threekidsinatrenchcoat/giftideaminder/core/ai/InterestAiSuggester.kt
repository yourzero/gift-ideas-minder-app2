package com.threekidsinatrenchcoat.giftideaminder.core.ai

/**
 * Interface for AI-powered interest suggestions
 */
interface InterestAiSuggester {
    /**
     * Get AI-generated interest suggestions based on a parent category label
     * @param parentLabel The parent category to generate suggestions for
     * @return List of suggested interest labels
     */
    suspend fun getSuggestions(parentLabel: String): List<String>
}