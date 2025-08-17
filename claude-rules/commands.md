# Build and Development Commands

## Build & Deploy
```bash
./gradlew build                    # Build project
./gradlew installDebug            # Install on device/emulator
./gradlew clean                   # Clean build
```

## Testing
```bash
./gradlew test                    # Unit tests
./gradlew connectedAndroidTest    # Instrumentation tests
./gradlew testDebugUnitTest --tests "com.giftideaminder.viewmodel.PersonFlowViewModelTest"  # Specific test
```

## Linting and Code Quality
```bash
./gradlew lintDebug               # Lint checks
./gradlew lintDebug --continue    # Generate lint report
```

## SDK Path Configuration (Critical)
**Environment Detection Required**: Check if you're in WSL/Linux vs Windows environment before builds.

- **WSL/Linux environment**: Set `sdk.dir=/home/justin/Android/Sdk` in local.properties
- **Windows environment**: Set `sdk.dir=D\\:\\\\Users\\\\justin\\\\AppData\\\\Local\\\\Android\\\\Sdk`

Change before Claude-initiated builds, restore after completion.