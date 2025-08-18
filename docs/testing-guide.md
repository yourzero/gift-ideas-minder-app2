# Testing Guide - New Features (August 2025)

## Quick Test Instructions for 7 Implemented Features

### Feature 1: Budget-respecting AI suggestions
**Branch**: `feature/budget-respecting-ai-suggestions`  
**Test Steps:**
1. Create a person with interests (e.g., "cooking", "Nike shoes size 10")
2. Add a gift idea for them with a low budget (e.g., $20)
3. Request AI suggestions - should get budget-appropriate items only
4. Try with higher budget ($100+) - should get more expensive suggestions
5. **Expected**: Suggestions stay within specified budget range

### Feature 2: Prompt for occasion when missing
**Branch**: `feature/occasion-prompting`  
**Test Steps:**
1. Add a new gift idea without specifying an occasion
2. Request AI suggestions
3. **Expected**: System should prompt "What's the occasion?" or default to "birthday"
4. AI suggestions should be tailored to the occasion (birthday vs anniversary vs holiday)

### Feature 3: Interests & Inspirations list (General vs Specific)
**Branch**: `feature/interests-general-specific`  
**Test Steps:**
1. Go to Person Details screen
2. Add general interests: "cooking", "sports", "music"
3. Switch to "Specific" tab and add: "Nike Air Max 90", "iPhone 15 Pro"
4. **Expected**: Two separate lists with proper categorization
5. Toggle between General/Specific tabs to verify filtering

### Feature 4: Mark specifics as "already owned"
**Branch**: `feature/already-owned-marking`  
**Test Steps:**
1. In Person Details, add specific interest: "PlayStation 5"
2. **Expected**: See "Mark Owned/Available" button next to specific items
3. Click "Mark Owned" - status should change to "Already owned" (red text)
4. Request AI suggestions for this person
5. **Expected**: AI should avoid suggesting items marked as owned

### Feature 5: Inline auto-complete from contacts
**Branch**: `feature/contacts-autocomplete`  
**Test Steps:**
1. Go to "Add Person" screen
2. Start typing a name that matches your device contacts
3. **Expected**: Dropdown appears with contact suggestions
4. Select a contact - should auto-fill name and other details
5. **Note**: Requires CONTACTS permission - grant when prompted

### Feature 6: Simple vs Advanced mode for Interests
**Branch**: `feature/simple-advanced-interests-mode`  
**Test Steps:**
1. Go to Settings screen (bottom nav or person detail screen)
2. Toggle "Advanced Mode" OFF
3. **Expected**: Person details now only show general interests
4. Try adding interest - should only allow general categories
5. Toggle Advanced Mode ON
6. **Expected**: Both General and Specific tabs return with ownership tracking

### Feature 7: Prompt/tip to suggest Advanced mode
**Branch**: `feature/advanced-mode-prompt`  
**Test Steps:**
1. Ensure you're in Simple Mode (Settings → Advanced Mode OFF)
2. Add 3+ specific-sounding interests to a person:
   - "Nike Air Max shoes"
   - "iPhone 15 Pro Max"  
   - "PlayStation 5 console"
3. **Expected**: Dialog appears: "Enable Advanced Mode?"
4. Click "Enable Advanced Mode"
5. **Expected**: Automatically switches to advanced mode with all features

## Integration Test Scenarios

### Complete Workflow Test
1. **Setup**: Create person "John" with mixed interests in Advanced Mode
2. **Add Interests**: 
   - General: "gaming", "technology"
   - Specific: "Steam Deck", "AirPods Pro" (mark as owned)
3. **Create Gift**: Add gift idea for John, budget $150
4. **AI Test**: Request suggestions - should avoid owned items, respect budget
5. **Mode Test**: Switch to Simple Mode - should hide specific items/ownership

### Edge Case Testing
- **Empty State**: Test with person having no interests
- **Budget Edge**: Test $0 budget vs $10000 budget
- **Permission**: Test contacts without granting permission
- **Network**: Test AI suggestions without internet connection
- **Long Names**: Test with very long interest names/person names

## Expected Behaviors Summary

✅ **Budget Filtering**: AI never suggests items above specified budget  
✅ **Occasion Context**: AI suggestions match the specified occasion  
✅ **Interest Categories**: Clear separation between general vs specific interests  
✅ **Ownership Tracking**: Owned items excluded from AI suggestions  
✅ **Contact Integration**: Smooth autocomplete with device contacts  
✅ **Mode Switching**: Seamless transition between Simple/Advanced modes  
✅ **Smart Suggestions**: Automatic prompt to upgrade when users need advanced features  

## Troubleshooting

**AI suggestions not working**: Check internet connection and API key  
**Contacts not appearing**: Verify CONTACTS permission granted  
**Mode toggle not saving**: Check SettingsRepository and DataStore setup  
**Interests not filtering**: Verify InterestType enum and database queries  
**Budget not respected**: Check GeminiAIService prompt construction  

---
*Generated for Gift Idea Minder Android - Features implemented August 2025*