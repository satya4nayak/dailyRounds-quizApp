# 🧠 MCQ Quiz App

> A production-style Android quiz app built with **Kotlin + Jetpack Compose**, **MVI + DDD** architecture, and **Pure Dagger 2** dependency injection across a **multi-module Gradle** project.

---

## 📸 Overview

10-question MCQ quiz with a smooth light-themed UI. Questions are fetched from a mocked API (hardcoded JSON + 1 s delay). The full flow — Splash → Quiz → Results → Restart — is driven by a single `Activity`.

| Screen | What you see |
|--------|-------------|
| **Network Call Loader** | Network loader — animated dots while questions are fetched |
| **Quiz** | Progress bar, question card, letter-badged options, answer banner, skip button |
| **Results** | Score ring, count-up animation, stats (correct / skipped / best streak), performance badge, restart button |

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

### ✨ Features

- 🎨 **Light Material 3 UI** — custom blue-gray palette
- 🔤 **Letter-badged option cards** (A–D) with animated correct / wrong states
- 🔥 **Streak celebration** — streak ≥ 3 triggers a Lottie fire overlay, **auto-dismissed by the ViewModel** ; best streak persists across restarts given that app is not closed since the score is stored in uiState
- ✅ **Instant feedback** — Correct / Not quite! / Skipped
- 📊 **Animated progress bar**
- ⏩ **Auto-advance** — moves to the next question 2 s after answering
- 🏆 **Performance badge based on score** — 🏆 Gold(8+) / 🥈 Silver(5-7) / 🥉 Bronze (0-4)
- 🔁 **Clean restart** — `FLAG_ACTIVITY_CLEAR_TASK` wipes the back stack and boots a fresh `Activity` (and ViewModel) from scratch

---

## 🛠️ Tech Stack

| Library | Version | Role |
|---|---|---|
| **Kotlin** | 2.0.21 | Language |
| **Jetpack Compose BOM** | 2024.09.03 | UI toolkit |
| **Material 3** | via BOM | Design system |
| **Dagger 2** | 2.51.1 | DI (no Hilt) |
| **Kotlin Coroutines** | 1.8.1 | Async |
| **Lottie Compose** | 6.5.2 | Fire animation |
| **Min / Target SDK** | 28 / 35 | Android 9 → 15 |

---

## 🏗️ Architecture

**MVI + Domain-Driven Design** across 4 Gradle modules:

```
app/          ← Activity, ViewModelFactory, Dagger wiring
data/         ← DTO, mock API, repository impl, DataModule
feature/quiz/ ← Compose UI, ViewModel, domain state
```

### MVI Layer Contract

| Layer | File | Responsibility |
|---|---|---|
| **ViewModel** | `QuizViewModel` | Business logic; `Event → State` reductions; one-off `Effect`s |
| **ViewModelFactory** | `QuizViewModelFactory` | `isAssignableFrom` guard; bridges Dagger `Provider` → `ViewModelProvider.Factory` |
| **Activity** | `MainActivity` | Orchestrator: injects factory, `collectAsStateWithLifecycle()`, handles effects, maps raw callbacks → `handleEvent()` |
| **Screens** | `QuizEntryPoint` + screens | Passive UI — plain callbacks only (`onOptionSelected`, `onSkip`, `onRestart`); zero ViewModel knowledge |

### Module Graph

```
app  ──▶  feature:quiz  ──▶  data (domain models only cross boundaries)
app  ──▶  data
```

---

## 🔥 Streak System

- Badge (🔥 + count) always visible; pulses amber when streak ≥ 1
- **Streak ≥ 3** → full-screen Lottie fire overlay, **auto-dismissed by the ViewModel** at 1.5 s
- Celebration re-fires on every subsequent correct answer while streak stays ≥ 3
- Wrong answer resets streak to 0; skipping has no effect
- `longestStreak` is **preserved across restarts** via `allTimeLongestStreak` in the ViewModel companion object (process lifetime)

---

## 🧩 Dependency Injection (Pure Dagger 2)

| Module | Provides |
|---|---|
| `DataModule` | `QuestionApiClient`, `QuestionRepository` |
| `QuizModule` | `QuizService` (via `QuizAppService`) |
| `ViewModelModule` | `QuizViewModelFactory` (singleton; wraps `Provider<QuizViewModel>`) |
| `AppComponent` | `@Singleton` root; injects into `MainActivity` |

`MainActivity` receives `@Inject QuizViewModelFactory`, initialises it in `onCreate` after `inject(this)`, and passes it to `viewModel(factory = …)` inside `setContent`.

---

## ⚙️ Dependencies

All versions live in `gradle/libs.versions.toml`. Change a version there and re-sync — no need to touch individual `build.gradle.kts` files.

# MCQQuizApp — Data Flow Diagram for loading question

```
┌──────────────────────────────────────────────────────────────────────────────┐
│  UI LAYER                                                                    │
│                                                                              │
│  NetworkLoaderScreen  ◄── QuizEntryPoint (state.screen = Splash)             │
│  (Animated loading dots while state.isLoading = true)                        │
└──────────────────────────────────────────────────────────────────────────────┘
                                        ▲
                                        │ StateFlow<QuizUiState> (isLoading=true)
                                        │
┌──────────────────────────────────────────────────────────────────────────────┐
│  VIEWMODEL LAYER                                                             │
│                                                                              │
│  QuizViewModel.loadQuestions()                                               │
│   └─ setState { isLoading = true }                                           │
│   └─ viewModelScope.launch { quizService.loadQuestions() }                   │
│   └─ on success: setState { questions=..., isLoading=false, screen=Quiz }    │
│   └─ on error:   emitEffect(Effect.ShowQuestionLoadError(msg))               │
└──────────────────────────────────────────────────────────────────────────────┘
                                        │
                                        │ suspend fun loadQuestions()
                                        ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│  APPLICATION SERVICE LAYER  (feature:quiz)                                   │
│                                                                              │
│  QuizAppService : QuizService                                                │
│   └─ fun loadQuestions(): List<Question>                                     │
│       └─ delegates to questionRepository.getQuestions()                      │
└──────────────────────────────────────────────────────────────────────────────┘
                                        │
                                        │ suspend fun getQuestions()
                                        ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│  REPOSITORY LAYER  (data module)                                             │
│                                                                              │
│  QuestionRepositoryImpl : QuestionRepository                                 │
│   └─ fun getQuestions(): List<Question>                                      │
│       └─ apiClient.fetchQuestions()           ──► returns List<QuestionDto>  │
│       └─ dtos.map { it.toDomainModel() }      ──► returns List<Question>     │
└──────────────────────────────────────────────────────────────────────────────┘
                                        │
                                        │ suspend fun fetchQuestions()
                                        ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│  API / INFRA LAYER  (data module)                                            │
│                                                                              │
│  QuestionApiService : QuestionApiClient                                      │
│   └─ suspend fun fetchQuestions(): List<QuestionDto>                         │
│       └─ delay(1000ms)   [simulates network latency]                         │
│       └─ returns hardcoded mock QuestionDto list                             │
│                                                                              │
│  QuestionDtoMapper                                                           │
│   └─ QuestionDto.toDomainModel() → Question                                  │
│       (maps: id, question, options, correctOptionIndex)                      │
└──────────────────────────────────────────────────────────────────────────────┘
```
