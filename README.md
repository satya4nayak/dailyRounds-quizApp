# 🧠 MCQ Quiz App

> A polished, production-style Android quiz app built with **Kotlin + Jetpack Compose**, clean **MVVM + DDD** architecture, and **Pure Dagger 2** dependency injection — all wired across a **multi-module Gradle** project.

---

## 📸 App Overview

MCQ Quiz App delivers a 10-question multiple-choice quiz experience with a smooth, animated **light-themed UI**. Questions are fetched from a mocked network layer (hardcoded JSON with a simulated 1-second delay), and the entire quiz flow — Splash → Quiz → Results — is managed through a single `Activity` with a sealed-class screen state.

| Screen | Description |
|--------|-------------|
| **Splash** | Brain emoji logo, app name, info pills, and animated loading dots while questions load |
| **Quiz** | Step dots, progress bar, question card with accent bar, letter-badged option cards with color-coded feedback, answer banner, and skip button |
| **Results** | Score ring, count-up animation, stats card (correct / skipped / streak), performance badge, and restart button |

### ✨ Key Features

- 🎨 **Light Material3 UI** with a custom blue-gray colour palette
- 🔤 **Letter-badged option cards** (A / B / C / D) with left accent bar and animated correct/wrong states
- 🔥 **Streak celebration** — every 3 correct answers in a row triggers a full-screen card overlay with a Lottie fire animation
- ✅ **Answer feedback banner** — immediately shows Correct / Not quite! / Skipped after each answer
- 📊 **Step-dot progress indicator** + animated progress bar
- ⏩ **Auto-advance** — after answering, the next question loads automatically after 2 seconds
- 🏆 **Performance badge** — earns 🏆 Gold / 🥈 Silver / 🥉 Bronze based on final score
- 🔁 **Restart** the quiz without leaving the app

---

## 🛠️ Tech Stack

| Tool / Library | Version | Role |
|---|---|---|
| **Kotlin** | 2.0.21 | Primary language |
| **Jetpack Compose BOM** | 2024.09.03 | UI toolkit |
| **Material 3** | via Compose BOM | Design system |
| **Dagger 2** | 2.51.1 | Dependency Injection (no Hilt) |
| **Kotlin Coroutines** | 1.8.1 | Async / concurrency |
| **Kotlinx Serialization** | 1.7.3 | JSON parsing |
| **Lottie Compose** | 6.5.2 | Fire streak animation |
| **Android Gradle Plugin** | 8.7.0 | Build tooling |
| **Gradle** | 8.14.3 | Build system |
| **JDK** | 23 | Compile-time JVM |
| **Min SDK** | 28 (Android 9 Pie) | Minimum supported OS |
| **Target SDK** | 35 (Android 15) | Target OS |
| **Compile SDK** | 35 | Compile target |

---

## 🏗️ Architecture & Module Structure

The project enforces strict **MVVM + Domain-Driven Design** via a 4-module Gradle setup:

```
root/
├── app/            ← Android Application (Dagger wiring, MainActivity)
├── domain/         ← Pure Kotlin — models, repository ports, service interface
├── data/           ← Android library — DTO, fake API, repository impl, Dagger DataModule
└── feature/
    └── quiz/       ← Android library — all Compose UI, ViewModel, state
```

### Module Dependency Graph

```
app  ──▶  feature:quiz
app  ──▶  data
feature:quiz  ──▶  domain
data          ──▶  domain
```

> **Hard rule:** `domain` has zero Android or third-party dependencies. Inter-layer currency is always **domain models only**.

---

## 📋 Requirements

Before you begin, make sure you have the following installed and configured:

| Requirement | Version | Notes |
|---|---|---|
| **Android Studio** | Meerkat (2024.3) or newer | Recommended: latest stable |
| **JDK** | **23** (required) | JDK 24/25/26 will cause Kotlin 2.0.x parser errors |
| **Gradle** | 8.14.3 | Managed by the Gradle wrapper — no manual install needed |
| **Android Gradle Plugin** | 8.7.0 | Declared in `gradle/libs.versions.toml` |
| **Kotlin** | 2.0.21 | Declared in `gradle/libs.versions.toml` |
| **Android SDK** | API 35 (Android 15) | Install via Android Studio SDK Manager |
| **Android Emulator** | API 28+ (Android 9+) | Or a physical device running Android 9+ |

> ⚠️ **Critical:** The Gradle daemon must run on **JDK 23**. The project sets this automatically via `gradle.properties` (`org.gradle.java.home`). If you have a different JDK installed at the path below, update it:
> ```properties
> # gradle.properties
> org.gradle.java.home=C:\\Program Files\\Java\\jdk-23
> ```

---

## 🚀 Getting Started — Clone to Running on Emulator

### Step 1 — Clone the Repository

```bash
git clone https://github.com/<your-username>/MCQQuizApp.git
cd MCQQuizApp
```

### Step 2 — Open in Android Studio

1. Launch **Android Studio**
2. Click **File → Open** and select the `MCQQuizApp` folder
3. Wait for Gradle sync to complete (it will download all dependencies automatically)

> If Gradle sync fails with a JDK error, go to **File → Project Structure → SDK Location** and set the **Gradle JDK** to JDK 23.

### Step 3 — Install Required SDK (if not already installed)

1. Go to **Tools → SDK Manager**
2. Under **SDK Platforms**, install **Android 15 (API 35)**
3. Under **SDK Tools**, ensure **Android Emulator** and **Android SDK Build-Tools 35** are installed

### Step 4 — Create an Emulator (if needed)

1. Go to **Tools → Device Manager → Create Device**
2. Choose a phone (e.g., **Pixel 8**)
3. Select a **system image with API 28 or higher** (Android 9 Pie or later)
4. Finish and launch the emulator

### Step 5 — Run the App

**Option A — Android Studio (recommended):**
1. Select your emulator or connected device from the device dropdown
2. Click the **▶ Run** button (or press `Shift + F10`)

**Option B — Command Line:**
```bash
# Windows (PowerShell)
.\gradlew.bat installDebug

# macOS / Linux
./gradlew installDebug
```

The app will be installed and launched automatically on your emulator or connected device.

### Step 6 — Build a Debug APK (optional)

```bash
# Windows
.\gradlew.bat assembleDebug

# macOS / Linux
./gradlew assembleDebug
```

Output APK: `app/build/outputs/apk/debug/app-debug.apk`

---

## 📱 Supported Android Versions

| Android Version | API Level | Support |
|---|---|---|
| Android 9 Pie | API 28 | ✅ Minimum supported |
| Android 10 | API 29 | ✅ |
| Android 11 | API 30 | ✅ |
| Android 12 | API 31 | ✅ |
| Android 12L | API 32 | ✅ |
| Android 13 | API 33 | ✅ |
| Android 14 | API 34 | ✅ |
| Android 15 | API 35 | ✅ Target / recommended |

---

## 📂 Package Structure

```
# data/
com.assignment.mcqquiz.data/
├── domain/model/   Question.kt
├── source/         QuizApiService.kt (hardcoded JSON + 1s delay)
├── mapper/         QuestionMapper.kt
├── repository/     QuestionRepositoryImpl.kt
└── di/             DataModule.kt

# feature/quiz/
com.assignment.mcqquiz.feature.quiz/
├── domain/
│   ├── contract/   QuizAppContract.kt (sealed nav state)
│   └── state/      QuizUiState.kt
├── ui/
│   ├── screen/     SplashScreen.kt, QuizScreen.kt, ResultScreen.kt
│   │               QuizEntryPoint.kt
│   ├── component/  OptionCard.kt, ProgressBar.kt, StreakBadge.kt,
│   │               StreakCelebrationOverlay.kt, TopBar.kt
│   ├── theme/      QuizTheme.kt, QuizColor.kt, QuizType.kt
│   └── viewmodel/  QuizViewModel.kt

# app/
com.assignment.mcqquiz/
├── MainActivity.kt
├── QuizApplication.kt
└── di/             AppComponent.kt
```

---

## 🎨 Design System

The app uses a **light theme** (`lightColorScheme`). All colour tokens are defined in `feature/quiz/ui/theme/QuizColor.kt`:

### Background & Surface

| Token | Hex | Usage |
|---|---|---|
| `Background` | `#EEF2F7` | App/screen background |
| `SurfaceColor` | `#FFFFFF` | Cards, top bar, option backgrounds |
| `SurfaceHi` | `#F4F7FB` | Elevated surfaces, skip button bg, step dots |
| `BorderColor` | `#D5DCE8` | Card borders, dividers |

### Accent

| Token | Hex | Usage |
|---|---|---|
| `Primary` | `#4F7EF7` | Buttons, progress bar, active dots, question eyebrow |

### Answer Feedback

| Token | Hex | Usage |
|---|---|---|
| `CorrectGreen` | `#1DAA60` | Correct answer text / icon |
| `CorrectBg` | `#1A1DAA60` (10%) | Correct option background |
| `CorrectBorder` | `#661DAA60` (40%) | Correct option border |
| `WrongRed` | `#E84545` | Wrong answer text / icon |
| `WrongBg` | `#1AE84545` (10%) | Wrong option background |
| `WrongBorder` | `#66E84545` (40%) | Wrong option border |

### Streak / Amber

| Token | Hex | Usage |
|---|---|---|
| `Amber` / `StreakActive` | `#E07B00` | Active streak badge, celebration card, longest streak stat |
| `AmberBg` | `#1AE07B00` (10%) | Streak badge background |
| `AmberBorder` | `#66FFB340` (40%) | Streak badge / celebration card border |

### Text

| Token | Hex | Usage |
|---|---|---|
| `TextPrimary` | `#1A2340` | Headings, question text, score number |
| `TextSecondary` | `#5B6B8A` | Body text, labels, skip button text |
| `TextMuted` | `#9AA5BE` | Hints, counters, timestamps |

---

## 🔥 Streak System

- The **streak badge** (🔥 + count) is always visible in the header of the quiz screen
  - **Idle** (streak = 0): light `SurfaceHi` background, muted border
  - **Active** (streak ≥ 1): amber background, amber border, pulse animation
- When your streak reaches **3, 6, 9, ...** (any multiple of 3), a **full-screen card overlay** appears:
  - Dark backdrop with a white card, amber border
  - Lottie fire animation (120 dp)
  - *"You're on fire!"* title + *"X in a row!"* badge
  - Auto-dismisses after 1.5 seconds (or tap to dismiss early)
- **Skipping** a question does **not** reset or increment the streak
- **Wrong answers** reset the streak to 0

---

## 🧩 UI Components

| Component | File | Description |
|---|---|---|
| `ScreenTopBar` | `TopBar.kt` | Reusable 20 dp white bar + 1 dp divider pinned at the top of Quiz and Result screens |
| `OptionCard` | `OptionCard.kt` | Answer option with letter badge (A–D), left accent bar, animated correct/wrong/highlight states |
| `QuizProgressBar` | `ProgressBar.kt` | Animated 5 dp rounded progress track |
| `StreakBadge` | `StreakBadge.kt` | Idle / active pill badge with pulse animation |
| `StreakCelebrationOverlay` | `StreakCelebrationOverlay.kt` | Full-screen dark backdrop + card with Lottie fire, title, badge, and countdown |

All components include `@Preview` functions covering their key visual states.

---

## ⚙️ Gradle & Dependency Configuration

All dependency versions are managed in a single version catalog:

```
gradle/libs.versions.toml
```

To update a dependency, change the version there and re-sync Gradle — no need to hunt through individual `build.gradle.kts` files.

---

## 🧩 Dependency Injection (Pure Dagger 2)

No Hilt. Dagger 2 is wired manually:

- **`DataModule`** → provides `QuizApiService`, `QuestionRepository`
- **`AppModule`** → provides `QuizService` (implemented by `QuizAppService`)
- **`AppComponent`** → `@Singleton` component that injects into `MainActivity`
- `MainActivity` receives a `Provider<QuizViewModel>` and uses a `ViewModelProvider.Factory` to hook it into the Compose lifecycle
