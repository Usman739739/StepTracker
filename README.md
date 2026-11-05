# StepTracker — Android Step Tracking App

A production-ready Android app built with Kotlin and Jetpack Compose that tracks physical activity, counts steps, automatically pauses during biking/driving, and allows scheduling tracking times. Features an elegant home-screen widget built with Jetpack Glance.

---

## ✨ Features

### Step Counting
- **Hardware Step Counter** (`TYPE_STEP_COUNTER`) as primary sensor for maximum accuracy
- **Accelerometer Fallback** with peak-detection algorithm when step sensor is unavailable
- **Foreground Service** ensures continuous tracking even when app is closed
- **Persistent Notification** with live step count and pause/resume action

### Bike/Vehicle Detection
- **Activity Recognition Transition API** detects ON_BICYCLE and IN_VEHICLE transitions
- **Auto-pause** step counting when biking or driving is detected
- **Software filter** with minimum step interval (250ms) rejects road bumps
- **Dual-layer approach** combines API detection with signal processing

### Automatic Scheduling
- Set daily time ranges (e.g., 8:00 AM – 6:00 PM) for active tracking
- Uses `AlarmManager.setExactAndAllowWhileIdle` for precision in Doze mode
- **WorkManager** periodic verification ensures resilience after reboots/kills
- Toggle to enable/disable scheduling

### Home-Screen Widget
- Built with **Jetpack Glance** (Compose-style widget API)
- Modern dark card design with rounded corners
- Step count display with progress bar
- Live tracking status indicator (Walking, Biking, Paused, etc.)
- Tap to open app
- Auto-updates every 15 minutes + on step count changes

### Health Connect Integration
- Syncs step data to **Health Connect** (modern replacement for Google Fit)
- Reads/writes `StepsRecord` entries
- Other health apps can access your step data

---

## 🏗 Architecture

```
┌─────────────────────────────────────────────────┐
│                   UI Layer                        │
│  Compose Screens │ ViewModels │ Glance Widget    │
├─────────────────────────────────────────────────┤
│                Domain Layer                       │
│  Repositories │ ScheduleManager │ ActivityHelper  │
├─────────────────────────────────────────────────┤
│                 Data Layer                        │
│  DataStore │ Room DB │ Health Connect │ Sensors   │
├─────────────────────────────────────────────────┤
│              Service Layer                        │
│  ForegroundService │ WorkManager │ AlarmManager   │
└─────────────────────────────────────────────────┘
```

- **MVVM** with `ViewModel` + `StateFlow`
- **Hilt** for dependency injection
- **DataStore** for user preferences
- **Room** for step history caching
- **Coroutines** throughout

---

## 📁 Project Structure

```
StepTracker/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/steptracker/app/
│       │   ├── StepTrackerApp.kt          # Hilt Application
│       │   ├── MainActivity.kt            # Compose entry point
│       │   ├── di/                        # Hilt modules
│       │   ├── data/
│       │   │   ├── local/                 # Room DB, DataStore
│       │   │   └── repository/            # Repositories, Health Connect
│       │   ├── service/
│       │   │   ├── StepCounterService.kt  # Foreground service
│       │   │   └── PeakDetectionFilter.kt # Accelerometer step filter
│       │   ├── activity/
│       │   │   ├── ActivityRecognitionHelper.kt
│       │   │   └── ActivityTransitionReceiver.kt
│       │   ├── scheduling/
│       │   │   ├── ScheduleManager.kt     # AlarmManager wrapper
│       │   │   └── ScheduleWorker.kt      # WorkManager resilience
│       │   ├── receiver/                  # BroadcastReceivers
│       │   ├── ui/
│       │   │   ├── theme/                 # Colors, Typography, Theme
│       │   │   ├── navigation/            # NavGraph
│       │   │   ├── screens/               # Compose screens
│       │   │   ├── components/            # Reusable UI components
│       │   │   └── viewmodel/             # ViewModels
│       │   ├── widget/                    # Glance widget
│       │   └── util/                      # Constants, helpers
│       └── res/
├── build.gradle.kts                       # Project-level
├── settings.gradle.kts
└── README.md
```

---

## 🔧 Setup & Build

### Prerequisites
- **Android Studio** Ladybug (2024.2.1) or newer
- **JDK 17** or newer
- **Android SDK** with API 35 (compileSdk)
- **Google Play Services** on test device (for Activity Recognition API)

### Build Steps

1. **Clone / Open the project** in Android Studio
2. **Sync Gradle** — Android Studio will download all dependencies
3. **Run on device** (API 26+ / Android 8.0+)

> ⚠️ Health Connect requires either the standalone app or a device with Google Play Services. Emulators may not have Health Connect available.

### Generate APK

```bash
./gradlew assembleRelease
```

The signed APK will be in `app/build/outputs/apk/release/`.

---

## 🔐 Permissions

The app requests these permissions with clear explanations during onboarding:

| Permission | Purpose |
|---|---|
| `ACTIVITY_RECOGNITION` | Step counting and bike/drive detection |
| `POST_NOTIFICATIONS` | Persistent notification with live step count |
| `FOREGROUND_SERVICE` | Background tracking service |
| `FOREGROUND_SERVICE_HEALTH_CONNECTED` | Health data foreground service type |
| `RECEIVE_BOOT_COMPLETED` | Restart tracking after device reboot |
| `SCHEDULE_EXACT_ALARM` | Precise schedule alarm timing |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | Prevent service from being killed |
| `health.READ_STEPS` | Read steps from Health Connect |
| `health.WRITE_STEPS` | Write steps to Health Connect |

### Granting Permissions

1. **Onboarding screen** guides users through each permission
2. **Activity Recognition** — standard runtime permission dialog
3. **Notifications** — Android 13+ runtime permission dialog
4. **Health Connect** — opens Health Connect permission controller
5. **Battery Optimization** — opens system battery exemption dialog

---

## 🏥 Health Connect Setup

Health Connect acts as the centralized health data store. The app:

1. **Writes** step records to Health Connect every 5 minutes while tracking
2. **Reads** today's total steps from Health Connect on startup for sync

### Manifest Configuration

The `AndroidManifest.xml` already includes:
- `health.READ_STEPS` and `health.WRITE_STEPS` permissions
- `ACTION_SHOW_PERMISSIONS_RATIONALE` intent filter
- `VIEW_PERMISSION_USAGE` activity alias for permission deep-linking
- `<queries>` block for Health Connect package visibility

### Testing Health Connect

- Install the **Health Connect** app from Play Store if not pre-installed
- On Android 14+, Health Connect is built into the system
- Use the Health Connect developer settings to view written data

---

## 🧪 Testing

### Unit Tests
```bash
./gradlew test
```

### Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

### Manual Testing Checklist

- [ ] Step count updates in real-time while walking
- [ ] Notification shows live step count
- [ ] Pause/Resume from notification works
- [ ] Service survives app swipe-away
- [ ] Service restarts after device reboot
- [ ] Bike/drive detection pauses counting
- [ ] Schedule starts/stops service at configured times
- [ ] Widget displays correct data and updates
- [ ] Daily reset at midnight works
- [ ] Health Connect sync writes data correctly
- [ ] Widget "Grant permissions" state when permissions denied
- [ ] Onboarding flow completes correctly

---

## 📋 Technical Details

### Step Detection Algorithm

**Primary (TYPE_STEP_COUNTER):**
- Hardware-level step counter, most accurate
- Reports cumulative steps since last reboot
- Delta is computed between readings

**Fallback (Accelerometer Peak Detection):**
- Low-pass filter (α = 0.15) smooths signal
- Magnitude = √(x² + y² + z²) − 9.81
- Peak detection with minimum valley-to-peak delta (3.0 m/s²)
- Minimum step interval: 250ms (rejects road bumps)

### Service Resilience

1. **START_STICKY** — system restarts service if killed
2. **BootReceiver** — restarts service after device reboot
3. **ScheduleWorker** — periodic WorkManager job verifies correct state
4. **Foreground notification** — highest priority for survival

### Widget Update Strategy

1. **Periodic**: WorkManager job every 15 minutes (minimum allowed)
2. **Event-driven**: Service triggers update when step count changes by 50+
3. **AlarmManager**: `updatePeriodMillis` in widget info XML

---

## 📝 License

This project is provided as-is for educational and commercial use.
