# Architecture (AI-Optimized)

Tech: Compose+Material3, MVVM, Room+LocalDate, Hilt, Coroutines

Packages: `data/` (API,DAO,entities,repos) `di/` `ui/` (components,screens,nav,theme) `viewmodel/`

**Critical Rules:**
- ALWAYS LocalDate (never timestamps) - stored as epochDay via TypeConverter
- Lists: `§`-delimited strings + TypeConverter
- PersonRole: bitmask enum (SELF|RECIPIENT|GIFTER|COLLABORATOR|CONTACT_ONLY)
- Relations: RelationshipType + ImportantDate entities, use ImportantDateDao.replaceForPerson()
- DB: destructive migrations (debug), proper migrations (prod), version 1
- State: ViewModels→StateFlow/Flow, Compose→remember+collectAsState
- Nav: AppNavGraph+SnackbarHostState, `showSnackbarAndPopBackStack()`