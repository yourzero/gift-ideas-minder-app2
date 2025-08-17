# Suggested Commands for Gift Idea Minder

## Build Commands
- `./gradlew build` - Full project build
- `./gradlew installDebug` - Install debug APK to device/emulator
- `./gradlew clean` - Clean build artifacts

## Testing Commands
- `./gradlew test` - Run unit tests
- `./gradlew connectedAndroidTest` - Run instrumented tests on device/emulator
- `./gradlew testDebugUnitTest --tests "ClassName"` - Run specific test class

## Code Quality Commands
- `./gradlew lintDebug` - Run lint checks
- `./gradlew lintDebug --continue` - Run lint and continue despite errors for full report

## Environment Setup
**CRITICAL**: SDK path must be configured in local.properties:
- Windows: `sdk.dir=D\\:\\\\Users\\\\justin\\\\AppData\\\\Local\\\\Android\\\\Sdk`
- WSL/Linux: `sdk.dir=/home/justin/Android/Sdk`

## Development Workflow
1. Build project: `./gradlew build`
2. Run tests: `./gradlew test`
3. Run lint: `./gradlew lintDebug`
4. Install to device: `./gradlew installDebug`

## Troubleshooting Commands
- `./gradlew clean build` - Clean rebuild if build issues
- Check SDK path in local.properties if build fails