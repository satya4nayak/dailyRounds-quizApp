# 🧠 MCQ Quiz App

> A production-style Android quiz app built with **Kotlin + Jetpack Compose**, **MVI + DDD** architecture, and **Pure Dagger 2** dependency injection across a **multi-module Gradle** project.

---

## 📸 Overview

A 10-question MCQ quiz with a smooth light-themed UI. Questions are fetched from a **real remote API** via Retrofit. The full flow — Loader → Quiz → Results → Restart — is driven entirely by a single `Activity` reacting to `Effect`s emitted by the ViewModel.

| Screen | What you see |
|--------|-------------|
| **Network Loader** | Animated dots while questions are fetched from the API |
| **Quiz** | Progress bar, question card, letter-badged options, answer banner, skip button |
| **Results** | Score ring, count-up animation, stats (correct / skipped / best streak), performance badge, restart button |
| **Error** | Full-screen ⚠️ banner with message and a Retry button |

---

## 🚀 Getting Started

```bash
git clone https://github.com/<your-username>/MCQQuizApp.git
```

1. Open the `MCQQuizApp` folder in **Android Studio**
2. Ensure Gradle JDK is set to **JDK 23** (`File → Project Structure → SDK Location`)
3. Install **Android SDK 35** via SDK Manager if needed
4. Run on an emulator or device with **API 28+**

```bash
# Build & install
.\gradlew.bat installDebug        # Windows
./gradlew installDebug            # macOS / Linux
```

> ⚠️ JDK 24+ causes Kotlin 2.0.x parser errors. Keep JDK 23 set in `gradle.properties`:
> `org.gradle.java.home=C:\\Program Files\\Java\\jdk-23`

---

## ✨ Features

- 🌐 **Real API** — questions fetched from a remote Gist via Retrofit + OkHttp (URL defined in `build.gradle.kts`, accessed via `BuildConfig`)
- 🎨 **Light Material 3 UI** — custom blue-gray palette
- 🔤 **Letter-badged option cards** (A–D) with animated correct / wrong reveal states
- 🔥 **Streak celebration** — streak ≥ 3 triggers a Lottie fire overlay, auto-dismissed by the ViewModel at 1.5 s, then question advances at 2 s; best streak persists across restarts within the same process
- ✅ **Instant answer feedback** — Correct / Not quite! / Skipped banners
- 📊 **Animated progress bar**
- ⏩ **Auto-advance** — moves to the next question 2 s after answering
- 🏆 **Performance badge** — 🏆 Gold (8+) / 🥈 Silver (5–7) / 🥉 Bronze (0–4)
- ⚠️ **Error screen** — full-screen banner with retry; shown on network failure or empty response
- 🔁 **Clean restart** — `FLAG_ACTIVITY_CLEAR_TASK` wipes the back stack and boots a fresh `Activity` (and ViewModel)
- 🛡️ **Config-change safe** — API is called only once per app launch; rotation / theme switch does not re-trigger the network call

---

## 🛠️ Tech Stack

| Library | Version | Role |
|---|---|---|
| **Kotlin** | 2.0.21 | Language |
| **Jetpack Compose BOM** | 2024.09.03 | UI toolkit |
| **Material 3** | via BOM | Design system |
| **Dagger 2** | 2.51.1 | DI (no Hilt) |
| **Kotlin Coroutines** | 1.8.1 | Async / `viewModelScope` |
| **Retrofit** | 2.11.0 | Type-safe HTTP client |
| **OkHttp** | 4.12.0 | HTTP engine (timeouts, connection pooling) |
| **KotlinX Serialization JSON** | 1.7.3 | JSON ↔ data class conversion |
| **Lottie Compose** | 6.5.2 | Fire streak animation |
| **Min / Target SDK** | 28 / 35 | Android 9 → 15 |

> **Retrofit vs OkHttp** — they are complementary, not alternatives. Retrofit provides the type-safe Kotlin interface + coroutine support + serialization. OkHttp is the underlying HTTP engine that handles raw connections, timeouts, and pooling. Retrofit always delegates to OkHttp; providing your own `OkHttpClient` gives you explicit control over timeouts and interceptors.

---

## 🏗️ Architecture

**MVI + Domain-Driven Design** across 3 Gradle modules:

```
app/          ← Activity, ViewModelFactory, AppComponent (Dagger root)
data/         ← DTO, Retrofit API service, repository impl, NetworkModule
feature/quiz/ ← Compose screens (dumb), ViewModel, domain state, DI modules
```

### MVI Contract

```
User Action
    │
    ▼  Event (sealed interface)
QuizViewModel.handleEvent()
    │
    ├──▶ _uiState: StateFlow<QuizUiState>   ← pure quiz-session data (questions, streaks, counts)
    │
    └──▶ _effects: SharedFlow<Effect>        ← navigation + one-shot signals (replay = 1)
              │
              └── ShowLoader / NavigateToQuiz / NavigateToResults / ShowError / RestartGame
```

### Module Graph

```
app  ──▶  feature:quiz  ──▶  data
app  ──▶  data
```

`Retrofit`, `OkHttpClient`, and `KotlinX JSON` are declared as `api(...)` in `:data` so that Dagger's kapt in `:app` can resolve the types when building the component graph.

---

## 🔥 Streak System

- Streak badge (🔥 + count) always visible on the quiz screen
- **Streak ≥ 3** → Lottie fire overlay, auto-dismissed by ViewModel at 1.5 s, then question advances at 2 s
- Celebration re-fires on every correct answer while streak stays ≥ 3
- Wrong answer resets streak to 0; skipping does not affect streak
- `longestStreak` preserved across restarts via `allTimeLongestStreak` in the ViewModel companion (process lifetime)

---

## 🌐 API & Networking

| Detail | Value |
|---|---|
| **Endpoint** | Defined as `QUIZ_API_BASE_URL` in `app/build.gradle.kts` → `BuildConfig.QUIZ_API_BASE_URL` |
| **Format** | JSON array of question objects |
| **Client** | Retrofit + KotlinX Serialization converter |
| **Timeouts** | 60 s connect / read / write (set on `OkHttpClient`) |
| **Error handling** | Any exception or empty list → `Effect.ShowError` → full-screen error banner |

---

## 🧩 Dependency Injection (Pure Dagger 2)

| Module | Provides |
|---|---|
| `NetworkModule` | `OkHttpClient`, `Retrofit`, `QuestionApiService` |
| `DataModule` | `QuestionRepository` |
| `QuizModule` | `QuizService` (via `QuizAppService`) |
| `ViewModelModule` | `QuizViewModelFactory` (singleton) |
| `AppComponent` | `@Singleton` root; injects into `MainActivity` |

`MainActivity` receives `@Inject QuizViewModelFactory`, calls `inject(this)` in `onCreate`, then passes the factory to `viewModel(factory = …)` inside `setContent`.

---

## 🗂️ Data Flow

```
MainActivity (LaunchedEffect → Event.InitialLoad)
        │
        ▼
QuizViewModel.loadQuestions()
   ├── tryEmit(Effect.ShowLoader)          → NetworkLoaderScreen shown
   └── viewModelScope.launch {
           quizService.loadQuestions()
               └── QuizAppService
                       └── QuestionRepository
                               └── QuestionApiService (Retrofit → OkHttp → Remote API)
                                       └── JSON deserialized to List<QuestionDto>
                                               └── mapped to List<Question>
           on success → _uiState.update(questions) + emit(NavigateToQuiz)
           on empty   → emit(ShowError)
           on error   → emit(ShowError)
       }
        │
        ▼
MainActivity collects Effect via SharedFlow (replay=1)
   → renders matching Composable screen
```

---

## ⚙️ Versions & Dependencies

All versions live in `gradle/libs.versions.toml`. Change a version there and re-sync — no need to touch individual `build.gradle.kts` files.

---

## 🧪 Tests

```bash
.\gradlew.bat test        # Windows
./gradlew test            # macOS / Linux
```

- **`QuizViewModelTest`** — 40+ unit tests covering loading, option selection, streaks, auto-advance, skip, restart, and effect sequencing using `StandardTestDispatcher` + Turbine
- **`QuestionApiServiceTest`** — verifies the Retrofit service delegates correctly and propagates exceptions
