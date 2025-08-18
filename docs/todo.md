# Gift Idea Minder – TODOs (2025-08-18)

## Features

- [ ] **Budget-respecting AI suggestions**
  - Update: `GiftSuggestionRepository` (AI prompt builder / result filter)
  - Ensure generated suggestions are constrained by the budget field in `Gift` or `Event`
  - Add fallback handling (if no suggestion within budget, return "no match")

- [ ] **Prompt for occasion when missing**
  - Update: `GiftSuggestionRepository` (AI prompt builder)
  - If no `occasion` is passed from UI, prompt user in `AddGiftScreen` (Compose)
  - Provide default (e.g., `"birthday"`) if user cancels

- [ ] **Interests & Inspirations list (General vs Specific)**
  - Update: `Person` model + Room DB (`PersonDao`, `PersonRepository`)
    - Add new table: `Interest(id, personId, type: String [General|Specific], value: String, alreadyOwned: Boolean)`
  - Update: `PersonViewModel` to load/save interests
  - Update: `PersonDetailScreen` (Compose) to show list of interests with type toggle (General/Specific)

- [ ] **Mark specifics as "already owned"**
  - Add `alreadyOwned` boolean to `Interest` entity
  - Update: `PersonDetailScreen` with checkbox for each Specific interest
  - Ensure AI prompt excludes items marked `alreadyOwned`

- [ ] **Inline auto-complete from contacts (lower priority)**
  - Update: `AddPersonScreen` (Compose)
  - Integrate Android Contacts API query while typing name
  - Show dropdown of matches; allow selecting to auto-fill person details

## To-Dos / UX Improvements

- [ ] **Simple vs Advanced mode for Interests**
  - Add toggle in `SettingsScreen` (Compose)
  - Store in `AppSettingsRepository` (Room or DataStore)
  - Simple Mode: only allow "General" entries
  - Advanced Mode: allow General + Specific entries with "already owned" flag

- [ ] **Prompt/tip to suggest Advanced mode**
  - Update: `PersonDetailScreen`
  - If user adds 3+ specific examples while in Simple mode, show tip/snackbar suggesting Advanced mode


## NEW

- [ ] fix: the general/specific tabs have been added to the new interests edit screen, but the reciepient add flow itself is using the old style of interests
- [ ] fix: the gift ideas screen coming from the person list screen:
  - hitting plus takes me to add a new gift, when it should let me add an interest
  - the UI could be much better, smoother, cleaner
- [ ] fix: the actual add gift flow screen - the "pick date" and "what's the occasion" steps are duplicate functionality - i think we can lose the "what's the occasion" screen, and the user can add occasions on the date screen. Unless you can think of a good reason we need both. I'm not sure what it's even for, because AI gift suggestion are done for a person, not for a gift idea - because a gift idea is the *result* of ai suggestions, not the source
- [ ] fix: add/edit person - gift inspirations screen, the current interests doesn't scroll. also, tighten up the UI, especially the padding between each current interest
- [ ] fix: add person screen, inline autocomplete doesn't seem to be working at all; at worst, add debug logging to the autocomplete code so I can see if it's even firing
- 