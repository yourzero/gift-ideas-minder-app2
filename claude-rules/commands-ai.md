# Commands (AI-Optimized)

Build: `./gradlew build`
Install: `./gradlew installDebug`
Clean: `./gradlew clean`
Test: `./gradlew test` + `./gradlew connectedAndroidTest`
Lint: `./gradlew lintDebug` + `--continue` for reports
Specific test: `./gradlew testDebugUnitTest --tests "com.giftideaminder.viewmodel.PersonFlowViewModelTest"`

**SDK Path**: Check env before builds, change before Claude builds, restore after
- WSL/Linux: `sdk.dir=/home/justin/Android/Sdk`
- Windows: `sdk.dir=D\\:\\\\Users\\\\justin\\\\AppData\\\\Local\\\\Android\\\\Sdk`