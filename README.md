# habit-tracker

Minimalist Android habit tracker built with Kotlin and Jetpack Compose.

## Features

- Daily check-off habits with current and longest streaks
- 16-week activity heatmap on the habit detail screen (tap any past day to toggle)
- 8-color habit palette + optional emoji label
- Modal bottom sheet for adding and editing habits
- Warm-neutral light theme and matching dark theme
- Local-only storage (Room)

## Stack

- **Kotlin** 2.1.0, **Jetpack Compose** with **Material 3** (Compose BOM 2024.12.01)
- **Navigation Compose** for screen routing
- **Room** 2.6.1 (KSP) for persistence
- **kotlinx-datetime** for date math
- **Android Gradle Plugin** 8.7.3, Gradle 8.10.2 (Kotlin DSL)
- Single-Activity architecture, edge-to-edge display
- `compileSdk` / `targetSdk` 35, `minSdk` 26

## Build

Requires JDK 17+ and the Android SDK (`platforms;android-35`, `build-tools;35.0.0`, `platform-tools`).

```sh
./gradlew assembleDebug
```

The APK is produced at `app/build/outputs/apk/debug/app-debug.apk`.

## Install on a connected device

```sh
./gradlew installDebug
```
