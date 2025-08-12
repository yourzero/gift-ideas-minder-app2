# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Gift Idea Minder is an Android application built with Jetpack Compose, Room, Hilt, and MVVM architecture. The app helps users capture, organize, and track gift ideas for friends and family with features including AI-powered suggestions, price tracking, OCR import, and comprehensive person/relationship management.

## Build and Development Commands

### Building and Running
```bash
# Build the project
./gradlew build

# Run on connected device/emulator
./gradlew installDebug

# Clean build
./gradlew clean
```

### Testing
```bash
# Run unit tests
./gradlew test

# Run instrumentation tests
./gradlew connectedAndroidTest

# Run specific test
./gradlew testDebugUnitTest --tests "com.giftideaminder.viewmodel.PersonFlowViewModelTest"
```

### Linting and Code Quality
```bash
# Run lint checks
./gradlew lintDebug

# Generate lint report
./gradlew lintDebug --continue
```

## Architecture Overview

### Core Technologies
- **UI**: Jetpack Compose with Material3 theming
- **Architecture**: MVVM with unidirectional data flow
- **Database**: Room with LocalDate support via TypeConverters
- **Dependency Injection**: Hilt
- **Navigation**: Compose Navigation
- **Async**: Kotlin Coroutines and Flows
- **Network**: Retrofit with Gson for AI and price services

### Package Structure
```
com.threekidsinatrenchcoat.giftideaminder/
├── data/
│   ├── api/           # Network services (AIService, PriceService)
│   ├── converter/     # Room TypeConverters
│   ├── dao/           # Database access objects
│   ├── model/         # Room entities and database
│   └── repository/    # Data repositories
├── di/                # Hilt dependency injection modules
├── ui/
│   ├── components/    # Reusable UI components
│   ├── navigation/    # Navigation setup and routes
│   ├── screens/       # Screen composables
│   └── theme/         # Material3 theming
└── viewmodel/         # UI state management
```

### Key Architectural Patterns

#### Database Design
- Uses `LocalDate` stored as epochDay with Room TypeConverter for date handling
- Implements relationship-based person management with `RelationshipType` and `ImportantDate` entities
- Database version 1 with destructive migrations enabled for debug builds
- Transactional date replacement via `ImportantDateDao.replaceForPerson()`

#### State Management
- ViewModels expose UI state via StateFlow/Flow
- Uses `remember` and `collectAsState` for Compose integration
- Implements loading, success, and error states consistently

#### Navigation
- Central `AppNavGraph` with shared `SnackbarHostState`
- Multi-step flows (e.g., Add/Edit Recipient) managed by dedicated ViewModels
- Utility function `showSnackbarAndPopBackStack()` for consistent UX

## Development Standards

### Data Model Conventions
- **Dates**: Always use `LocalDate`, never timestamps or epoch milliseconds
- **Lists**: Store as delimited strings with TypeConverter (`§` delimiter)
- **Person Roles**: Use bitmask pattern with `PersonRole` enum (`SELF`, `RECIPIENT`, `GIFTER`, `COLLABORATOR`, `CONTACT_ONLY`)
- **Database Migrations**: Specify Room migrations for schema changes in production

### Code Style
- Follow existing Kotlin conventions in the codebase
- Use meaningful, self-documenting variable and function names
- Maintain MVVM separation: ViewModels handle business logic, Composables handle UI
- Include all necessary imports in file changes
- Use `@Composable` functions with appropriate modifiers and state hoisting

### UI/UX Patterns
- Relationship-first Add Person flow: ask relationship type, then prompt relevant dates
- Use Material3 components and theming consistently
- Implement proper loading and error states
- Follow accessibility guidelines with content descriptions

### Testing Requirements
- Unit tests for ViewModels focusing on state transitions
- Instrumentation tests for complex user flows
- Mock external dependencies (AI services, price APIs)
- Test database operations and migrations

## Key Features and Flows

### Person Management
- Multi-step Add/Edit Person flow driven by `PersonFlowViewModel`
- Relationship-based date prompting (birthday for friends, anniversary for spouses)
- Custom date support with labels
- Contact import integration with SMS scanning capability

### Gift Management  
- Gift tracking with price history via `PriceRecord` entities
- AI-powered suggestions through `AIService` integration
- OCR import from screenshots using ML Kit Text Recognition
- Budget tracking and spending alerts

### Integration Points
- **AI Service**: Gemini API for gift suggestions (requires `GEMINI_API_KEY` in local.properties)
- **Price Tracking**: CamelCamelCamel API integration for price history
- **ML Kit**: Text recognition for OCR import functionality
- **Contacts**: Device contact integration with permission handling

## Configuration

### Required Local Properties
Add to `local.properties`:
```properties
GEMINI_API_KEY=your_api_key_here
AI_ENABLED=true
```

### Permissions
The app requires these permissions (declared in AndroidManifest.xml):
- `READ_SMS` - for SMS scanning feature
- `READ_EXTERNAL_STORAGE` - for file import
- `READ_CONTACTS` - for contact integration

## Common Development Tasks

### Adding New Entities
1. Create Room entity in `data/model/`
2. Add DAO in `data/dao/` with appropriate queries
3. Update `AppDatabase` entities list and version
4. Create repository in `data/repository/`
5. Add Hilt bindings in `di/RepositoryModule`
6. Update ViewModels to use new repository

### Database Schema Changes
1. Increment version in `AppDatabase`
2. For production: Create migration in `AppDatabase.companion`
3. For development: Use destructive migration (already enabled)
4. Update TypeConverters if needed

### Adding New Screens
1. Create Composable in `ui/screens/`
2. Add route to `AppNavGraph`
3. Create ViewModel if state management needed
4. Update navigation calls from existing screens

This project follows clean architecture principles adapted for Android development with Jetpack Compose, emphasizing maintainable code, proper testing, and excellent user experience.