# Current Architecture and Structure

## Project Overview

The Gift Idea Minder Android app uses Jetpack Compose for UI, Room for persistence, Hilt for dependency injection, and follows a clean architecture pattern adapted to the existing structure. It implements unidirectional data flow with ViewModels managing state via Flows and Coroutines.

Key features include gift management (Epic 1), person management (Epic 2), basic reminders (Epic 3), integrations/import (Epic 4), budgeting (Epic 5), AI-driven suggestions (Epic 6), and partial price tracking (Epic 7), with navigation handled via Compose Navigation.

Recent addition: a relationship-first Add/Edit Giftee flow (4-step wizard: Relationship → Details → Dates → Review) driven by `PersonFlowViewModel`, with important date prompts and shared snackbar handling.

## Architecture Layers

### Data Layer
- Handles data persistence and retrieval using Room, plus network APIs for AI and price tracking.
- Located in `app/src/main/java/com/giftideaminder/data/`.
- Components:
  - **Models**: `Gift.kt`, `Person.kt`, and new entities `RelationshipType.kt` (flags `hasBirthday`, `hasAnniversary`) and `ImportantDate.kt` (label + `LocalDate`).
  - **DAO**: `GiftDao.kt`, `PersonDao.kt`, plus new `RelationshipTypeDao.kt` and `ImportantDateDao.kt` with a transactional `replaceForPerson(personId, dates)` API.
  - **Repository**: `GiftRepository.kt`, `PersonRepository.kt`, plus new `RelationshipTypeRepository.kt` (with `ensureSeeded()`) and `ImportantDateRepository.kt`.
  - **Database**: `AppDatabase.kt` for Room setup (schema version 4). In dev builds we enable destructive migrations; proper migrations should be restored for release builds.
  - **API**: Services like `AIService.kt` and `PriceService.kt`.
  - **Converter**: `Converters.kt` for `LocalDate` and `List<String>`, `PriceHistoryConverter.kt` for price history.

### Presentation Layer
- Manages UI and user interactions using Jetpack Compose.
- Located in `app/src/main/java/com/giftideaminder/ui/` and `viewmodel/`.
- Components:
  - **Screens**: `GiftListScreen.kt`, `AddEditGiftScreen.kt`, `PersonListScreen.kt`, and new `AddEditGifteeFlowScreen.kt` (relationship-first 4-step wizard). Other screens: `ImportScreen.kt`, `BudgetScreen.kt`, `GiftDetailScreen.kt`, dashboards.
  - **Components**: `GiftItem.kt`, `PersonItem.kt`, `SuggestionsCarousel.kt`.
  - **Navigation**: `AppNavGraph.kt` routes `add_person` and `edit_person/{personId}` to `AddEditGifteeFlowScreen`. `AppScaffold.kt` owns a shared `SnackbarHostState` passed to `AppNavGraph`, with a central `showSnackbarAndPopBackStack(message)` util used by the flow.
  - **Theme**: Material3 theming in `theme/`.
  - **ViewModels**: `GiftViewModel.kt`, `PersonViewModel.kt`, and new `PersonFlowViewModel.kt` (manages steps, prompts, picked dates, and persistence). `PersonFlowViewModel` seeds relationship types and derives prompts from flags.

### Dependency Injection
- Uses Hilt for DI.
- Located in `app/src/main/java/com/giftideaminder/di/`.
- Modules: `DatabaseModule.kt`, `RepositoryModule.kt`, `NetworkModule.kt` (for AI and price services).

### Main Entry Points
- `MainApplication.kt`: Application class with Hilt setup.
- `MainActivity.kt`: Entry activity setting up Compose content and navigation.

### Additional Configurations
- **Permissions**: Added READ_SMS and READ_EXTERNAL_STORAGE in AndroidManifest.xml for Epic 4.
- **Dependencies**: ML Kit Text Recognition and OpenCSV for Epic 4 imports, Retrofit and Gson for network APIs (via libs.versions.toml).
- **Database dev setup**: Destructive migrations enabled only for debug builds; restore proper migrations for production.

## Folder Structure

```
gift-idea-minder-android--cursor/
  - app/
    - build.gradle.kts
    - src/
      - main/
        - AndroidManifest.xml
        - java/
          - com/
            - giftideaminder/
              - data/
                - api/
                  - AIService.kt
                  - PriceService.kt
                - converter/
                  - PriceHistoryConverter.kt
                - dao/
                  - GiftDao.kt
                  - PersonDao.kt
                - model/
                  - AppDatabase.kt
                  - Gift.kt
                  - Person.kt
                - repository/
                  - GiftRepository.kt
                  - PersonRepository.kt
              - di/
                - DatabaseModule.kt
                - NetworkModule.kt
                - RepositoryModule.kt
              - MainActivity.kt
              - MainApplication.kt
              - ui/
                - components/
                  - GiftItem.kt
                  - PersonItem.kt
                  - SuggestionsCarousel.kt
                - navigation/
                  - Navigation.kt
                - screens/
                  - AddEditGiftScreen.kt
                  - BudgetScreen.kt
                  - DashboardScreenMock.kt
                  - GiftDetailScreen.kt
                  - GiftListScreen.kt
                  - HomeDashboardGenerated_Chatgpt.kt
                  - HomeDashboardGenerated.kt
                  - ImportScreen.kt
                  - PersonListScreen.kt
                - theme/
                  - Color.kt
                  - Shape.kt
                  - Theme.kt
                  - Type.kt
              - viewmodel/
                - GiftViewModel.kt
                - ImportViewModel.kt
                - PersonViewModel.kt
        - res/
          - drawable/
            - edit_gift___select_occasions_modal.xml
            - ic_launcher_foreground.xml
          - mipmap-anydpi-v26/
            - ic_launcher_round.xml
            - ic_launcher.xml
          - mipmap-hdpi/
          - mipmap-mdpi/
          - mipmap-xhdpi/
          - mipmap-xxhdpi/
          - mipmap-xxxhdpi/
          - values/
            - strings.xml
            - themes.xml
        - scripts/
          - s2c.sh
        - svg/
          - Edit Gift - Select Occasions Modal.svg
          - Edit Gift B.svg
          - Edit Giftee A.svg
          - Edit Giftee B.svg
          - Edit Giftee C.svg
          - Event Details.svg
          - Giftees.svg
          - Home-Dashboard.svg
          - myiconpack/
            - src/
              - gift-idea-minder-android/
                - __.git.kt
                - git/
                  - __Hooks.kt
                  - __Info.kt
                  - __Logs.kt
                  - __Objects.kt
                  - __Refs.kt
                  - logs/
                    - __Refs.kt
                    - refs/
                      - __Heads.kt
                      - __Remotes.kt
                      - remotes/
                        - __Origin.kt
                  - objects/
                    - ___02.kt
                    - ___1a.kt
                    - ___2f.kt
                    - ___53.kt
                    - ___56.kt
                    - ___62.kt
                    - ___72.kt
                    - ___85.kt
                    - ___8b.kt
                    - ___9a.kt
                    - __Ac.kt
                    - __Af.kt
                    - __Be.kt
                    - __Ca.kt
                    - __D9.kt
                    - __De.kt
                    - __E2.kt
                    - __E9.kt
                    - __Info.kt
                    - __Pack.kt
                  - refs/
                    - __Heads.kt
                    - __Remotes.kt
                    - __Tags.kt
                    - remotes/
                      - __Origin.kt
  - build.gradle.kts
  - docs/
    - android-jetpack-compose-cursorrules-prompt-file/
      - android---project-structure.mdc
      - android-jetpack-compose---general-best-practices.mdc
      - android-jetpack-compose---performance-guidelines.mdc
      - android-jetpack-compose---testing-guidelines.mdc
      - android-jetpack-compose---ui-guidelines.mdc
    - architecture.md
    - clean-code.md
    - current-architecture.md
    - Project-design.md
  - gradle/
  - gradle.properties
  - gradlew
  - gradlew.bat
  - README.md
  - settings.gradle.kts
```

## Key Best Practices Followed
- Unidirectional data flow: ViewModels expose Flows for state, UI collects and reacts.
- State hoisting: State managed in ViewModels, passed to composables.
- Clean code: Single responsibility (separate ViewModels for Gift, Person, Import), meaningful names, no magic numbers.
- Compose guidelines: Proper use of remember, Modifiers, theming, previews (to be added).
- Async handling: Coroutines for imports/parsing in Epic 4, AI fetches, and price updates.
- Testing: Guidelines suggest unit tests for ViewModels (to be implemented).

## Recent Improvements

### Relationship-first Add/Edit Giftee Flow (Wizard)
- New `AddEditGifteeFlowScreen` with steps: Relationship → Details → Dates → Review.
- `PersonFlowViewModel` handles step transitions, derives date prompts from `RelationshipType` flags, tracks `pickedDates`, and persists via `ImportantDateRepository.replaceForPerson`.
- Shared snackbar handling via `AppScaffold` and `AppNavGraph` on success.
- Dates step supports custom labeled dates with inline edit/remove and a reusable `DatePickerRow` with initial value and clear.

## Flow UI / UX Walkthrough
### LocalDate Migration (Database v2)
- **Migrated Person.birthday from Long to LocalDate**: Eliminates timezone issues and off-by-one errors common with epoch milliseconds.
- **Room Type Converters**: Added `Converters.kt` with `LocalDate` ↔ `Long` (epoch days) conversion for efficient storage.
- **UTC Date Picker Integration**: Fixed Material3 DatePicker timezone handling by using UTC consistently in conversions.
- **Enhanced UI Components**: Updated `PersonItem.kt`, `AddEditRecipientScreen.kt`, and related components to use `LocalDate` and `DateTimeFormatter`.
- **Contact Integration**: `AddEditRecipientViewModel` includes contacts import, SMS scanning, and birthday parsing from contact data.

Below is a short walkthrough of the relationship-first Add/Edit Giftee flow. Place screenshots under `docs/images/add-edit-giftee/` with the given filenames to render inline.

- Relationship step
  - Pick a relationship from chips; default prompts are derived from `RelationshipType` flags.
  - Screenshot: ![Relationship step](images/add-edit-giftee/step1-relationship.png)

- Details step
  - Enter the giftee's name (and future fields when added).
  - Screenshot: ![Details step](images/add-edit-giftee/step2-details.png)

- Dates step
  - Shows prompted date rows (e.g., Birthday, Anniversary). Each row opens a date picker and supports clearing.
  - Add custom labeled dates via a text field + date picker; custom entries appear under “Other dates”.
  - Screenshot: ![Dates step - prompts](images/add-edit-giftee/step3-dates-prompts.png)
  - Screenshot: ![Dates step - custom](images/add-edit-giftee/step3-dates-custom.png)

- Review step
  - Summarizes relationship, name, and all dates. Save persists the person and replaces their important dates transactionally, then shows a snackbar and navigates back.
  - Screenshot: ![Review step](images/add-edit-giftee/step4-review.png)

Notes
- Success snackbar is shown by `AppNavGraph` using the `SnackbarHostState` owned by `AppScaffold`.
- In debug builds, Room destructive migrations are enabled, so wiping occurs when schema changes.

### Data model additions
- Entities: `RelationshipType` (seeded at first run), `ImportantDate`.
- DAOs: `RelationshipTypeDao`, `ImportantDateDao` with transactional replace.
- Repositories: `RelationshipTypeRepository`, `ImportantDateRepository`.

### Stability and dev setup
- Enabled destructive migrations in debug; bumped Room schema to v4.
- Minor `GiftViewModel` stubs to satisfy screen references.

### Testing
- Unit tests for `PersonFlowViewModel` (date pick/remove, prompt derivation, persistence orchestration).
- Instrumentation tests for the new flow (add person and custom date add/remove scenarios).
### Enhanced Recipient Management
- **AddEditRecipientScreen**: Full-featured person management with photo, birthday, relationships, notes, and contact integration.
- **Contact Picker Integration**: Users can import recipient data directly from device contacts.
- **SMS Scanning**: Optional AI-powered scanning of SMS history with selected contacts for gift ideas.
- **Relationship Management**: Multi-select dropdown for relationship categorization (Family, Friend, Coworker).

This structure adapts the recommended clean architecture while building on the initial project setup. For future expansions, consider adding a domain layer for use cases if business logic grows complex, runtime permission handling for Epic 4, full reminder scheduling for Epic 3, completing price tracking alerts (Epic 7), and implementing security features (Epic 8). 