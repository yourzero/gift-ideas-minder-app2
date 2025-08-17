# Quick Development Workflows

## Adding Database Entities
1. Create entity in `data/model/`
2. Add DAO in `data/dao/`
3. Update `AppDatabase` + version
4. Create repository in `data/repository/`
5. Add Hilt bindings in `di/RepositoryModule`
6. Update ViewModels

## Adding Screens
1. Create Composable in `ui/screens/`
2. Add route to `AppNavGraph`
3. Create ViewModel if needed
4. Update navigation calls

## Database Changes
1. Increment `AppDatabase` version
2. Production: Create migration in `AppDatabase.companion`
3. Development: Use destructive migration (enabled)
4. Update TypeConverters if needed