# Architecture (AI-Optimized)

Tech: Compose+Material3, MVVM, Room+LocalDate, Hilt, Coroutines

Packages: `data/` (API,DAO,entities,repos) `di/` `ui/` (components,screens,nav,theme) `viewmodel/`

**Critical Rules:**
- ALWAYS LocalDate (never timestamps)
- Lists: `§`-delimited strings + TypeConverter
- PersonRole: bitmask enum
- DB: destructive migrations (debug), proper migrations (prod)
- State: ViewModels→StateFlow/Flow, Compose→remember+collectAsState
- Nav: AppNavGraph+SnackbarHostState, `showSnackbarAndPopBackStack()`