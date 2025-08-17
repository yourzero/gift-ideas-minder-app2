# Coding Standards for Gift Idea Minder

## Code Style and Conventions
- **Language**: Kotlin with Jetpack Compose
- **Architecture**: MVVM pattern with Repository pattern
- **Dependency Injection**: Hilt annotations (@HiltAndroidApp, @AndroidEntryPoint)
- **Database**: Room with coroutines and Flow
- **UI**: Material 3 design system with Jetpack Compose

## Naming Conventions
- **Classes**: PascalCase (e.g., `GiftRepository`, `PersonViewModel`)
- **Functions**: camelCase (e.g., `getAllGifts()`, `addNewPerson()`)
- **Variables**: camelCase (e.g., `giftTitle`, `personName`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_AI_RETRIES`)

## File Organization
- **Packages**: Follow domain-driven structure
  - `data/`: Models, DAOs, repositories
  - `ui/`: Screens, components, themes
  - `viewmodel/`: ViewModels for UI state
  - `di/`: Dependency injection modules
  - `utils/`: Utility classes

## Code Quality Requirements
- Use proper error handling with try-catch blocks
- Implement logging for debugging (especially AI integration)
- Follow Material 3 design principles
- Use coroutines for async operations
- Implement proper Room type converters for complex data

## Testing Standards
- Unit tests for ViewModels and repositories
- Instrumented tests for database operations
- Use MockK for mocking in tests
- Test coroutines with kotlinx-coroutines-test