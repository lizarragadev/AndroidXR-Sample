# Android XR Demo - Event Schedule App

Android XR demo application for workshops and tech events. This app showcases Android XR capabilities with an adaptive UI and modern Material 3 design, designed to run on Android XR headsets and traditional Android devices.

## Features

### Core Features
- 📅 Event schedule by day with in-person/virtual mode indicators
- 👥 Speaker profiles with photos and bios
- 📱 Adaptive layout (single/two-pane for tablets and large screens)
- 🌓 Dark/Light theme toggle
- 📍 Venue location and virtual streaming info

### XR Features
- 🥽 **Floating Orbiters** - Movable panels with speaker and talk information
- 🎯 **Off-screen indicators** - Visual cues when Orbiters move outside viewport
- 🗺️ **ARCore Integration** - Place 3D Android robots on detected planes
- 🤖 **Procedural 3D Models** - Detailed Android robot with smooth geometry
- 👆 **Drag & Drop** - Move Orbiters freely, even outside screen bounds

## Tech Stack

- **Android XR** - Extended reality platform for headsets
- **ARCore** - Augmented reality with plane detection
- **OpenGL ES 2.0** - 3D rendering with GLSL shaders
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
- Device with ARCore support (for AR features)

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
├── ar/                # ARCore integration
│   ├── ModernARMapActivity.kt
│   └── rendering/     # OpenGL renderers
├── xr/                # XR features
│   ├── FloatingOrbiters/
│   └── SpeakerPhotoPanel.kt
├── data/              # Data models (Speaker, Talk)
├── repository/        # Data access layer
├── viewmodel/         # Business logic
├── ui/
│   ├── components/    # Reusable UI components
│   ├── screens/       # Main screens
│   └── theme/         # Theme and colors
└── navigation/        # Navigation graph
```

## XR Features Guide

### Floating Orbiters
- **Drag to move**: Press and drag Orbiters anywhere on screen
- **Move off-screen**: Orbiters can be moved outside visible area
- **Visual indicators**: Small colored circles appear at screen edges when Orbiters are off-screen
- **Close button**: Tap X to dismiss an Orbiter

### ARCore Demo
- **Tap AR icon**: Launch ARCore demo from toolbar
- **Plane detection**: Move device to detect horizontal planes (shown in light blue)
- **Place robots**: Tap anywhere on screen to place 3D Android robots
- **Toggle planes**: Hide/show detected planes with top-right button

## Customization

Edit the following files to customize for your event:
- `app/src/main/assets/speakers.json` - Speaker information
- `app/src/main/assets/talks.json` - Talk schedules
- `app/src/main/res/drawable/` - Speaker photos (PNG format)
- `app/src/main/java/.../ui/theme/Color.kt` - Theme colors

## License

MIT License
