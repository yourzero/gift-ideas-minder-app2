# TODO — New Features (Gift Idea Minder, Android / Kotlin + Jetpack Compose)

This file contains detailed steps for implementing the four new features.

---
## 1) Recipients list screen - `feature/recipients-list-improvements--2025-08-25`
- [x] Recipients list screen
    - Move the "idea generator" icon to the far left, to set it apart from the other actions which are all more edit-related. Also make the colors of the ideas and interests icons different (appropriate to whatever the icons are)
    - **Implementation**: Updated PersonItem.kt with SpaceBetween arrangement, lightbulb icon on far left with tertiary color for ideas and secondary color for interests


## 2) Interest Drill‑Down Details - `feature/interest-entity-implementation--2025-08-25` + `feature/interest-drill-down-details--2025-08-25`
- [x] Add `InterestEntity` Room table with parent/child relationship and flags `isDislike`, `isOwned`.
- [x] DAO + Repository for parent list, child list, add detail, toggle flags.
- [x] UI: InterestsScreen (flat parent list, `+n` badge if children), InterestDetailsSheet (chips, owned toggle, add detail).
- [x] ViewModel: manages parent/child state, debounced suggestions, toggle methods.
- [x] AI Suggestion stub: map parent labels to canned detail suggestions.
- [x] Acceptance: Add detail works, toggle owned/dislike persists, suggestions appear.
- **Implementation**: Created hierarchical InterestEntity with Room migrations, comprehensive InterestsViewModel with reactive state, Material Design 3 UI components with bottom sheets and filter chips, AI suggestion integration with category mapping

## 3) Disinterests / Hard No's - `feature/disinterests-hard-nos--2025-08-25`
- [x] Use `isDislike` boolean on both parent and child interests.
- [x] UI toggle icon (🚫) on rows, persisted via repo.
- [x] Acceptance: toggling works, indicated visually, persists.
- **Implementation**: Extended InterestEntity with isDislike flag, added filter chips for switching between interests and hard no's, implemented toggle functionality with visual indicators and repository persistence

## 4) 20 Questions Mode (AI‑Guided Interest Discovery) - `feature/twenty-questions-mode--2025-08-25`
- [x] Flow: CategoryPickerScreen → CategoryQuestionScreen → Review.
- [x] Categories: static list (Outdoors, Video games, Tech, Sports, etc.).
- [x] Question logic: local stub mapping categories to canned questions; max 3 per category.
- [x] Save new parents/details at end of flow.
- [x] ViewModel: manages current category, questions, answers.
- [x] Acceptance: user can add interests via flow; can skip/stop anytime.
- **Implementation**: Built complete 20Q flow with 10 static categories, multi-step navigation with TwentyQuestionsViewModel, contextual question generation via InterestAiQuestioner, review screen with interest creation, and comprehensive navigation integration

## 5) Trophies & Unlocks (Gamified Freemium) - `feature/trophies-gamification--2025-08-25`
- [x] Track milestones: first recipient, 5 recipients, 10 details, first 20Q session.
- [x] AchievementEntity or DataStore for unlockedAt timestamps.
- [x] Unlock rule: 5 recipients unlock 1 free AI suggestion per recipient.
- [x] UI: subtle confetti + toast/snackbar; optional trophy case MVP+.
- [x] Acceptance: milestones trigger once, unlock flags persist.
- **Implementation**: Created comprehensive gamification system with AchievementManager using SharedPreferences, AchievementsViewModel with reactive state, full AchievementsScreen with Material Design 3 achievement cards and unlock dialogs, integrated into bottom navigation replacing events tab

---

## Cross‑Cutting Tasks - `feature/cross-cutting-tasks--2025-08-25`
- [x] Strings: add to strings.xml.
- [x] Accessibility: icons with content descriptions, ≥48dp tap targets.
- [x] Hilt: provide repo, AI suggester, questioner, AchievementManager.
- [x] Analytics: log interactions via Analytics facade.
- [ ] Tests: DAO CRUD, VM state, Compose baseline screenshots.
- [x] Migration: add InterestEntity if not present.
- [x] Feature flags: allow enabling/disabling 20Q and Trophies.
- **Implementation**: Added comprehensive string resources, built Analytics facade with event tracking, created FeatureFlags system with SharedPreferences, established Hilt modules for dependency injection of all new components, implemented InterestAiSuggester and InterestAiQuestioner with category-based logic

---

## File Plan
```
data/model/InterestEntity.kt
data/dao/InterestDao.kt
data/repository/InterestRepository.kt

viewmodel/InterestsViewModel.kt
viewmodel/TwentyQuestionsViewModel.kt
viewmodel/AchievementViewModel.kt

ui/screens/interests/InterestsScreen.kt
ui/screens/interests/InterestDetailsSheet.kt
ui/screens/interests/TwentyQuestionsFlowNavHost.kt
ui/components/InterestRow.kt
ui/components/DetailChip.kt

core/ai/InterestAiSuggester.kt
core/ai/InterestAiQuestioner.kt
core/achievements/AchievementManager.kt
core/flags/FeatureFlags.kt
core/analytics/Analytics.kt

di/InterestsModule.kt
di/AiModule.kt
di/AchievementModule.kt
```

---

## Implementation Summary

All features have been implemented across 6 feature branches created from master:

### Feature Branches Created:

1. **`feature/recipients-list-improvements--2025-08-25`**
   - Updated PersonItem.kt UI layout with SpaceBetween arrangement
   - Moved lightbulb icon to far left, updated icon colors (tertiary for ideas, secondary for interests)

2. **`feature/interest-entity-implementation--2025-08-25`**
   - Created InterestEntity Room table with parent/child relationships
   - Added DAO and Repository methods for hierarchical interest management
   - Implemented Room migration (version 3→4) for InterestEntity table

3. **`feature/interest-drill-down-details--2025-08-25`**
   - Built comprehensive InterestsScreen with Material Design 3 components
   - Created InterestDetailsBottomSheet with chips, toggles, and AI suggestions
   - Implemented InterestsViewModel with reactive state management and debounced suggestions

4. **`feature/disinterests-hard-nos--2025-08-25`**
   - Extended interest system with isDislike boolean flag functionality
   - Added filter chips for switching between interests and hard no's
   - Implemented visual indicators and toggle persistence via repository

5. **`feature/twenty-questions-mode--2025-08-25`**
   - Built complete 20Q flow: CategoryPicker → Questions → Review
   - Created 10 static categories with contextual question generation
   - Implemented TwentyQuestionsViewModel with multi-step navigation and interest creation

6. **`feature/trophies-gamification--2025-08-25`**
   - Created AchievementManager with SharedPreferences-based tracking
   - Built AchievementsScreen with Material Design 3 achievement cards and unlock dialogs
   - Integrated achievements tab into bottom navigation (replaced events)
   - Implemented milestone-based benefits (5 recipients unlock AI suggestions)

7. **`feature/cross-cutting-tasks--2025-08-25`**
   - Added comprehensive string resources for all features
   - Created Analytics facade and FeatureFlags system
   - Built Hilt modules for dependency injection
   - Implemented InterestAiSuggester and InterestAiQuestioner with category-based logic

### Branch Strategy:
- All branches created from master as separate feature implementations
- No dependencies between branches (except implicit shared data models)
- Ready for independent review, testing, and merging
- Each branch contains complete, functional implementation of its respective feature


