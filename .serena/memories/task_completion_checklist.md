# Task Completion Checklist

When completing any development task on Gift Idea Minder:

## Code Quality Checks
1. **Build**: Run `./gradlew build` - must complete successfully
2. **Tests**: Run `./gradlew test` - all tests must pass
3. **Lint**: Run `./gradlew lintDebug` - fix any errors or warnings
4. **Code Review**: Ensure code follows established patterns and conventions

## Architecture Compliance
- Verify MVVM pattern is maintained
- Check proper dependency injection with Hilt
- Ensure Repository pattern is used for data access
- Validate proper use of Room database operations

## UI/UX Validation
- Test on device/emulator with `./gradlew installDebug`
- Verify Material 3 design consistency
- Check responsive layout across different screen sizes
- Validate navigation flows work correctly

## Integration Testing
- Test AI service integration if applicable
- Verify database operations work correctly
- Check that OCR functionality works as expected
- Validate any API integrations

## Documentation
- Update inline comments for complex logic
- Ensure proper Kotlin documentation for public APIs
- Update README if new features or setup steps added

## Final Verification
- Clean build: `./gradlew clean build`
- Full test suite: `./gradlew test connectedAndroidTest`
- Manual testing of changed functionality