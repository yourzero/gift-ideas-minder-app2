# Architecture Guidelines

## Tech Stack
- **UI**: Jetpack Compose + Material3
- **Architecture**: MVVM + unidirectional data flow
- **Database**: Room + LocalDate TypeConverters
- **DI**: Hilt
- **Navigation**: Compose Navigation
- **Async**: Coroutines + Flows
- **Network**: Retrofit + Gson

## Package Organization
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

## Critical Patterns to Follow

### Data Handling
- **ALWAYS use `LocalDate`** - never timestamps or epoch milliseconds
- Uses `LocalDate` stored as epochDay with Room TypeConverter for date handling
- **Lists**: Store as `§`-delimited strings with TypeConverter
- **Person Roles**: Use `PersonRole` enum bitmask pattern (`SELF`, `RECIPIENT`, `GIFTER`, `COLLABORATOR`, `CONTACT_ONLY`)
- **Database**: Use destructive migrations for debug, proper migrations for production
- Implements relationship-based person management with `RelationshipType` and `ImportantDate` entities
- Database version 1 with destructive migrations enabled for debug builds
- Transactional date replacement via `ImportantDateDao.replaceForPerson()`

### State Management
- Expose UI state via StateFlow/Flow from ViewModels
- Use `remember` + `collectAsState` in Compose
- Always implement loading/success/error states

### Navigation
- Use central `AppNavGraph` with shared `SnackbarHostState`
- Multi-step flows handled by dedicated ViewModels
- Use `showSnackbarAndPopBackStack()` for consistent UX