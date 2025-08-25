package com.threekidsinatrenchcoat.giftideaminder.core.ai

import com.threekidsinatrenchcoat.giftideaminder.core.analytics.Analytics
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AI-powered interest suggestion system.
 * Maps parent interest categories to relevant detail suggestions.
 */
@Singleton
class InterestAiSuggester @Inject constructor(
    private val analytics: Analytics
) {
    
    /**
     * Generate interest detail suggestions based on a parent category.
     * Currently uses static mapping but can be enhanced with actual AI.
     */
    suspend fun generateDetailSuggestions(
        parentLabel: String,
        existingDetails: List<String> = emptyList()
    ): List<String> {
        analytics.logEvent("ai_suggestions_requested", mapOf(
            "parent_label" to parentLabel,
            "existing_count" to existingDetails.size
        ))
        
        // Simulate AI processing delay
        delay(500)
        
        val allSuggestions = getSuggestionsForCategory(parentLabel.lowercase())
        
        // Filter out existing details and return up to 5 new suggestions
        return allSuggestions
            .filter { suggestion -> 
                existingDetails.none { existing -> 
                    existing.lowercase().contains(suggestion.lowercase()) ||
                    suggestion.lowercase().contains(existing.lowercase())
                }
            }
            .take(5)
    }
    
    private fun getSuggestionsForCategory(parentLabel: String): List<String> {
        return when (parentLabel) {
            "outdoors", "outdoor", "nature" -> listOf(
                "Hiking boots", "Camping gear", "National park passes", 
                "Outdoor photography equipment", "Trail mix", "Water bottles",
                "Binoculars", "Compass", "Sleeping bags", "Portable hammocks"
            )
            
            "gaming", "video games", "games" -> listOf(
                "Gaming headset", "Mechanical keyboard", "Gaming mouse",
                "RGB lighting", "Gaming chair", "Console controllers",
                "Gaming merchandise", "Collectible figures", "Art books", "Soundtracks"
            )
            
            "technology", "tech", "gadgets" -> listOf(
                "Smartphone accessories", "Wireless chargers", "Smart home devices",
                "Bluetooth speakers", "Wearable tech", "USB-C hubs",
                "Laptop stands", "Cable management", "Power banks", "Screen protectors"
            )
            
            "cooking", "food", "culinary" -> listOf(
                "Kitchen knives", "Cast iron cookware", "Spice sets",
                "Cookbooks", "Measuring tools", "Silicone utensils",
                "Food processors", "Coffee equipment", "Wine accessories", "Aprons"
            )
            
            "fitness", "exercise", "health" -> listOf(
                "Resistance bands", "Yoga mats", "Water bottles",
                "Fitness trackers", "Protein powders", "Workout clothes",
                "Foam rollers", "Home gym equipment", "Running shoes", "Recovery tools"
            )
            
            "reading", "books", "literature" -> listOf(
                "Bookmarks", "Reading lights", "Book stands",
                "Kindle accessories", "Bookends", "Library tote bags",
                "Reading journals", "Book sleeves", "Page holders", "Literary magazines"
            )
            
            "music", "audio" -> listOf(
                "High-quality headphones", "Vinyl records", "Concert tickets",
                "Music lessons", "Instrument accessories", "Audio interfaces",
                "Speaker systems", "Music streaming subscriptions", "Band merchandise", "Acoustic treatments"
            )
            
            "art", "creative", "crafts" -> listOf(
                "Quality brushes", "Professional paints", "Sketchbooks",
                "Digital drawing tablets", "Crafting supplies", "Storage solutions",
                "Easels", "Color palettes", "Art classes", "Gallery memberships"
            )
            
            "travel", "adventure" -> listOf(
                "Travel gear", "Luggage accessories", "Portable chargers",
                "Travel pillows", "Packing cubes", "Currency converters",
                "Travel journals", "Maps", "Language learning apps", "Travel experiences"
            )
            
            "pets", "animals" -> listOf(
                "Premium pet food", "Interactive toys", "Comfortable beds",
                "Training treats", "Grooming supplies", "Pet cameras",
                "Carrier bags", "Health supplements", "Pet clothing", "Veterinary care"
            )
            
            else -> listOf(
                "Gift cards", "Subscription services", "Experience vouchers",
                "Personalized items", "Quality accessories", "Hobby supplies"
            )
        }
    }
    
    /**
     * Get category-specific gift suggestions based on interests.
     */
    suspend fun generateGiftSuggestions(
        interests: List<String>,
        budget: Double? = null
    ): List<String> {
        analytics.logEvent("gift_suggestions_requested", mapOf(
            "interest_count" to interests.size,
            "has_budget" to (budget != null)
        ))
        
        // Simulate AI processing
        delay(800)
        
        val suggestions = mutableSetOf<String>()
        
        interests.forEach { interest ->
            suggestions.addAll(getSuggestionsForCategory(interest.lowercase()).take(3))
        }
        
        return suggestions.toList().take(8)
    }
}