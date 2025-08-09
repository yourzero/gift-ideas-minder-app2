# Android Architecture Guidelines

## UI Layer
- Use Jetpack Compose for new UI components
- Follow Material Design 3 guidelines
- Implement unidirectional data flow with ViewModels

### New flow: Add/Edit Giftee (relationship-first)
- Screen: `AddEditGifteeFlowScreen` implements a 4-step wizard: Relationship → Details → Dates → Review.
- ViewModel: `PersonFlowViewModel` manages steps and state, including:
  - Seeding and observing `RelationshipType` entries to populate options and derive date prompts (`hasBirthday`, `hasAnniversary`).
  - Tracking picked important dates as `Map<label, LocalDate>`; supports add, edit, clear.
  - Persisting via `PersonRepository` (insert/update) and `ImportantDateRepository.replaceForPerson` in a single logical action on Save.
- UX details:
  - Dates step provides per-label date pickers and a custom labeled date add (text + date picker), with inline review/edit/remove.
  - Review step presents relationship, name, and formatted dates before Save.
  - Success snackbar and navigation handled centrally via `AppNavGraph` using a shared `SnackbarHostState` from `AppScaffold`.

## Data Layer
- Use Room for local database storage
- Implement Repository pattern for data access
- Use Retrofit for network operations

### Entities and DAOs
- New `RelationshipType` entity (seeded defaults) with flags guiding UI prompts.
- New `ImportantDate` entity for labeled dates linked to a `Person` (FK with cascade delete).
- `RelationshipTypeDao` for reading and seeding; `ImportantDateDao` includes `replaceForPerson(personId, dates)` transactional API.

### Repositories
- `RelationshipTypeRepository` exposes `getAll()` and `ensureSeeded()`.
- `ImportantDateRepository` wraps `ImportantDateDao` including `replaceForPerson`.

### Database
- `AppDatabase` schema version bumped to 4. For development builds, destructive migrations are enabled; restore proper migrations for production.