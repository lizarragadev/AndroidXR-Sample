# Android XR Demo - Event Schedule App

Android XR demo application for workshops and tech events. This app showcases Android XR capabilities with an adaptive UI and modern Material 3 design, designed to run on Android XR headsets and traditional Android devices.

## Features

- 📅 Event schedule by day with in-person/virtual mode indicators
- 👥 Speaker profiles with photos and bios
- 📱 Adaptive layout (single/two-pane for tablets and large screens)
- 🌓 Dark/Light theme toggle
- 📍 Venue location and virtual streaming info
- 🎯 Optimized for Android XR headsets

## Tech Stack

- **Android XR** - Extended reality platform for headsets
- **Kotlin** - Modern Android development
- **Jetpack Compose** - Declarative UI
- **Material 3** - Design system
- **Navigation Compose** - Screen navigation
- **StateFlow** - Reactive state management
- **Gson** - JSON parsing
- **Coil** - Image loading

## Requirements

- Android Studio Hedgehog or newer
- Android SDK 34+
- Kotlin 2.1.0+
- Android XR device or emulator (for XR features)

## Build

```bash
./gradlew assembleDebug
```

## Install

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Project Structure

```
app/src/main/java/tech/lizza/demoxr/
├── data/           # Data models (Speaker, Talk)
├── repository/     # Data access layer
├── viewmodel/      # Business logic
├── ui/
│   ├── components/ # Reusable UI components
│   ├── screens/    # Main screens
│   └── theme/      # Theme and colors
└── navigation/     # Navigation graph
```

## Customization

Edit the following files to customize for your event:
- `app/src/main/assets/speakers.json` - Speaker information
- `app/src/main/assets/talks.json` - Talk schedules
- `app/src/main/res/drawable/` - Speaker photos (PNG format)
- `app/src/main/java/.../ui/theme/Color.kt` - Theme colors

## License

MIT License
