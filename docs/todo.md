# TODO — New Features (Gift Idea Minder, Android / Kotlin + Jetpack Compose)

This file contains detailed steps for implementing the four new features.

---
## 1) Recipients List Screen Improvements 
**Branch**: `feature/recipients-list-improvements`
- [ ] Move the "idea generator" icon to the far left, to set it apart from the other actions which are all more edit-related
- [ ] Make the colors of the ideas and interests icons different (appropriate to whatever the icons are)


## 2) Interest Drill‑Down Details (Guidance + Constraints)
**Branch**: `feature/interest-entity-implementation`
- [ ] Add `InterestEntity` Room table with parent/child relationship and flags `isDislike`, `isOwned`.
- [ ] DAO + Repository for parent list, child list, add detail, toggle flags.
- [ ] UI: InterestsScreen (flat parent list, `+n` badge if children), InterestDetailsSheet (chips, owned toggle, add detail).
- [ ] ViewModel: manages parent/child state, debounced suggestions, toggle methods.
- [ ] AI Suggestion stub: map parent labels to canned detail suggestions.
- [ ] Acceptance: Add detail works, toggle owned/dislike persists, suggestions appear.

## 3) Disinterests / Hard No's
**Branch**: `feature/disinterests-hard-nos`
- [ ] Use `isDislike` boolean on both parent and child interests.
- [ ] UI toggle icon (🚫) on rows, persisted via repo.
- [ ] Acceptance: toggling works, indicated visually, persists.

## 4) 20 Questions Mode (AI‑Guided Interest Discovery)
**Branch**: `feature/twenty-questions-mode`
- [ ] Flow: CategoryPickerScreen → CategoryQuestionScreen → Review.
- [ ] Categories: static list (Outdoors, Video games, Tech, Sports, etc.).
- [ ] Question logic: local stub mapping categories to canned questions; max 3 per category.
- [ ] Save new parents/details at end of flow.
- [ ] ViewModel: manages current category, questions, answers.
- [ ] Acceptance: user can add interests via flow; can skip/stop anytime.

## 5) Trophies & Unlocks (Gamified Freemium)
**Branch**: `feature/trophies-gamification`
- [ ] Track milestones: first recipient, 5 recipients, 10 details, first 20Q session.
- [ ] AchievementEntity or DataStore for unlockedAt timestamps.
- [ ] Unlock rule: 5 recipients unlock 1 free AI suggestion per recipient.
- [ ] UI: subtle confetti + toast/snackbar; optional trophy case MVP+.
- [ ] Acceptance: milestones trigger once, unlock flags persist.

---

## Cross‑Cutting Tasks
- [ ] Strings: add to strings.xml.
- [ ] Accessibility: icons with content descriptions, ≥48dp tap targets.
- [ ] Hilt: provide repo, AI suggester, questioner, AchievementManager.
- [ ] Analytics: log interactions via Analytics facade.
- [ ] Tests: DAO CRUD, VM state, Compose baseline screenshots.
- [ ] Migration: add InterestEntity if not present.
- [ ] Feature flags: allow enabling/disabling 20Q and Trophies.

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


