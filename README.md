# CopeCount ⏱️💼

**CopeCount** is a modern, privacy-focused Android application designed to track and manage specialized countdowns, internship timelines (such as **SIWES** — Students Industrial Work Experience Scheme), and custom work-week schedules.

Unlike standard calendar countdown apps that merely count calendar days, **CopeCount** calculates **actual working days** between start and end dates based on user-defined work schedules (e.g., Tuesday–Thursday or full work weeks), provides interactive daily clock-ins, calculates work streaks, and delivers scheduled reminder notifications.

---

## ✨ Features

- **💼 Custom Work Day Calculation**: Automatically calculates the exact number of active work days between a start and end date, ignoring non-working days.
- **⏱️ Interactive Daily Clock-In**:
  - One-tap daily clock-in with interactive pulsating animations and visual feedback.
  - Tracks logged dates, auto-computes completed weeks, and updates remaining days in real time.
- **🔥 Streak Tracking**: Monitors consecutive work day completion streaks to keep users motivated throughout their internship or project timeline.
- **🗂️ Multi-Profile Support**:
  - Create and manage multiple countdown profiles (e.g., "My SIWES", custom project milestones, work shifts).
  - Customize profile names, start dates, end dates, active work days, and notification preferences.
  - Seamlessly switch between active countdown profiles.
- **🔔 Notifications & Exact Alarms**:
  - Integrated `AlarmManager` and `ReminderReceiver` for timely daily check-in reminders.
  - Compatible with Android 13+ runtime notification permissions (`POST_NOTIFICATIONS`).
- **🎨 OLED Glassmorphic Design**:
  - Built with an OLED dark aesthetic (`#020617`), vibrant accents (Lilac, RichPurple, Gold), and frosted glass cards (`GlassCard`) with gradient borders.
  - Floating bottom swatch navigation with smooth Compose transitions between **Clock In**, **Dashboard**, and **Settings**.
- **🔒 Local & Offline-First**: All data is stored locally on-device via `SharedPreferences` and JSON serialization without third-party telemetry or cloud tracking.

---

## 🛠️ Architecture & Tech Stack

CopeCount is built following modern Android development practices with a clean MVVM architecture:

- **Language**: [Kotlin](https://kotlinlang.org/) (v2.2.10)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material Design 3](https://m3.material.io/)
- **Architecture**: MVVM (Model-View-ViewModel) with unidirectional data flow
- **State Management**: Compose State (`mutableStateOf`, `derivedStateOf`) & AndroidX ViewModel
- **Date & Time API**: `java.time` (`LocalDate`, `LocalDateTime`, `DayOfWeek`) with Core Library Desugaring enabled for older Android versions
- **Notifications & Background Tasks**: Android `AlarmManager`, `BroadcastReceiver`, and `WorkManager`
- **Data Persistence**: `SharedPreferences` + `Gson`
- **Build System**: Gradle (Kotlin DSL - `build.gradle.kts`) with Gradle Version Catalogs (`libs.versions.toml`)

---

## 📂 Project Structure

```text
CopeCount/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/copecount/
│   │   │   │   ├── MainActivity.kt                # App entry point & permission handling
│   │   │   │   ├── logic/
│   │   │   │   │   └── WorkDayCalculator.kt       # Working-day computation engine
│   │   │   │   ├── models/
│   │   │   │   │   └── CountdownProfile.kt        # Data model for countdown profiles
│   │   │   │   ├── notifications/
│   │   │   │   │   └── ReminderReceiver.kt        # BroadcastReceiver for scheduled alarms
│   │   │   │   ├── ui/
│   │   │   │   │   ├── CopeCountViewModel.kt      # Central app ViewModel & state logic
│   │   │   │   │   ├── MainScreen.kt              # Root scaffold & floating bottom swatch
│   │   │   │   │   ├── components/                # Glass cards, progress gauges, date picker
│   │   │   │   │   │   ├── DashboardComponents.kt
│   │   │   │   │   │   ├── DateSelectionBar.kt
│   │   │   │   │   │   └── SiwesCountdownScreen.kt
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   └── SettingsScreen.kt      # Profile & notification management
│   │   │   │   │   ├── tabs/
│   │   │   │   │   │   └── ClockInTab.kt          # Biometric-inspired clock-in screen
│   │   │   │   │   └── theme/                     # Color palette, Typography, & Theme
│   │   │   │   └── AndroidManifest.xml
│   │   │   └── res/                               # Icons, drawables, and XML resources
│   │   └── test/                                  # Unit tests
│   └── build.gradle.kts                           # App-level build configuration
├── gradle/
│   └── libs.versions.toml                         # Dependency version catalog
├── build.gradle.kts                               # Root build configuration
└── settings.gradle.kts                            # Project settings
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio**: Android Studio Ladybug (2024.2.1+) or newer
- **JDK**: Java 17 or higher
- **Android SDK Requirements**:
  - `minSdk`: **24** (Android 7.0 Nougat)
  - `targetSdk`: **35** (Android 15)
  - `compileSdk`: **35**

### Installation & Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/<your-username>/CopeCount.git
   cd CopeCount
   ```

2. **Open in Android Studio**:
   - Launch Android Studio.
   - Choose **File > Open...** and select the `CopeCount` project folder.
   - Wait for Gradle to download dependencies and sync the project.

3. **Build the Project**:
   - On Windows (PowerShell):
     ```powershell
     .\gradlew.bat assembleDebug
     ```
   - On macOS/Linux:
     ```bash
     ./gradlew assembleDebug
     ```

4. **Run the App**:
   - Connect a physical Android device (with USB Debugging enabled) or start an Android Virtual Device (AVD).
   - Click **Run (`Shift + F10`)** in Android Studio or run:
     ```powershell
     .\gradlew.bat installDebug
     ```

---

## 🧪 Testing

Execute local unit tests:
```powershell
.\gradlew.bat test
```

Execute connected instrumented tests on an emulator/device:
```powershell
.\gradlew.bat connectedAndroidTest
```

---

## 📋 Permissions

The application requests the following permissions:
- `android.permission.POST_NOTIFICATIONS`: To show reminder notifications on Android 13 (API 33) and above.
- `android.permission.SCHEDULE_EXACT_ALARM`: To schedule exact countdown and check-in reminders via `AlarmManager`.

---

## 📄 License

This project is licensed under the [MIT License](LICENSE) (or your preferred license).
