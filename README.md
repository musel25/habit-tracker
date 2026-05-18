# habit-tracker

Minimalist Android habit tracker built with Kotlin and Jetpack Compose.

## Status

First scaffold — single screen displaying "Habit Tracker". No habits or persistence yet.

## Stack

- **Kotlin** 2.1.0
- **Jetpack Compose** with **Material 3** (Compose BOM 2024.12.01)
- **Android Gradle Plugin** 8.7.3, Gradle 8.10.2 (Kotlin DSL)
- Single-Activity architecture, edge-to-edge display, Material You dynamic colors
- `compileSdk` / `targetSdk` 35, `minSdk` 26
- Version catalog (`gradle/libs.versions.toml`) for dependency management

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
