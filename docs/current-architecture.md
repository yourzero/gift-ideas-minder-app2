# Current Architecture and Structure

## Project Overview

The Gift Idea Minder Android app uses Jetpack Compose for UI, Room for persistence, Hilt for dependency injection, and follows a clean architecture pattern adapted to the existing structure. It implements unidirectional data flow with ViewModels managing state via Flows and Coroutines.

Key features include gift management (Epic 1), person management (Epic 2), basic reminders (Epic 3), and integrations/import (Epic 4), with navigation handled via Compose Navigation.

## Architecture Layers

### Data Layer
- Handles data persistence and retrieval using Room.
- Located in `app/src/main/java/com/giftideaminder/data/`.
- Components:
  - **Models**: Entity classes like `Gift.kt` (now with reminderOffset for Epic 3) and `Person.kt`.
  - **DAO**: Interfaces for database operations, e.g., `GiftDao.kt`, `PersonDao.kt`.
  - **Repository**: Abstraction over data sources, e.g., `GiftRepository.kt`, `PersonRepository.kt`.
  - **Database**: `AppDatabase.kt` for Room database setup (version bumped to 2 with migration for reminderOffset).

### Presentation Layer
- Manages UI and user interactions using Jetpack Compose.
- Located in `app/src/main/java/com/giftideaminder/ui/` and `viewmodel/`.
- Components:
  - **Screens**: Composable functions for main views, e.g., `GiftListScreen.kt`, `AddEditGiftScreen.kt`, `PersonListScreen.kt`, `AddEditPersonScreen.kt`, `ImportScreen.kt` (for Epic 4 imports).
  - **Components**: Reusable UI elements, e.g., `GiftItem.kt`, `PersonItem.kt`.
  - **Navigation**: `Navigation.kt` for app routing (add 'import' route to access ImportScreen).
  - **Theme**: Material Design theming in `theme/` (Color.kt, Shape.kt, Theme.kt, Type.kt).
  - **ViewModels**: Manage UI state, e.g., `GiftViewModel.kt`, `PersonViewModel.kt`, `ImportViewModel.kt` (for Epic 4 parsing/insertion).

### Dependency Injection
- Uses Hilt for DI.
- Located in `app/src/main/java/com/giftideaminder/di/`.
- Modules: `DatabaseModule.kt`, `RepositoryModule.kt`.

### Main Entry Points
- `MainApplication.kt`: Application class with Hilt setup.
- `MainActivity.kt`: Entry activity setting up Compose content and navigation.

### Additional Configurations
- **Permissions**: Added READ_SMS and READ_EXTERNAL_STORAGE in AndroidManifest.xml for Epic 4.
- **Dependencies**: Added ML Kit Text Recognition and OpenCSV for Epic 4 imports (via libs.versions.toml).

## Folder Structure

```
gift-idea-minder-android--cursor-2/
  - app/
    - build.gradle.kts
    - src/
      - main/
        - AndroidManifest.xml  # Updated with permissions for Epic 4
        - java/
          - com/
            - giftideaminder/
              - data/
                - dao/
                  - GiftDao.kt
                  - PersonDao.kt
                - model/
                  - AppDatabase.kt  # Updated for Epic 3 migration
                  - Gift.kt  # Updated with reminderOffset for Epic 3
                  - Person.kt
                - repository/
                  - GiftRepository.kt
                  - PersonRepository.kt
              - di/
                - DatabaseModule.kt
                - RepositoryModule.kt
              - MainActivity.kt
              - MainApplication.kt
              - ui/
                - components/
                  - GiftItem.kt
                  - PersonItem.kt
                - navigation/
                  - Navigation.kt
                - screens/
                  - AddEditGiftScreen.kt
                  - GiftDetailScreen.kt
                  - GiftListScreen.kt
                  - AddEditPersonScreen.kt
                  - PersonListScreen.kt
                  - ImportScreen.kt  # New for Epic 4
                - theme/
                  - Color.kt
                  - Shape.kt
                  - Theme.kt
                  - Type.kt
              - viewmodel/
                - GiftViewModel.kt
                - PersonViewModel.kt
                - ImportViewModel.kt  # New for Epic 4
        - res/
          - drawable/
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
    - Project-design.md
    - current-architecture.md  # This file
  - gradle/
    - libs.versions.toml  # Updated for Epic 4 dependencies
    - wrapper/
      - gradle-wrapper.jar
      - gradle-wrapper.properties
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
- Async handling: Coroutines for imports/parsing in Epic 4.
- Testing: Guidelines suggest unit tests for ViewModels (to be implemented).

This structure adapts the recommended clean architecture while building on the initial project setup. For future expansions, consider adding a domain layer for use cases if business logic grows complex, runtime permission handling for Epic 4, and full reminder scheduling for Epic 3. 