# 🧠 MCQ Quiz App
---

## 📸 Overview

A multi-category MCQ quiz app with a smooth light-themed UI. Categories and questions are fetched from a **real remote API** via Retrofit + OkHttp. Progress (NOT_STARTED / IN_PROGRESS / COMPLETED) is persisted in a local **Room database** and merged with category metadata on the list screen. The entire navigation flow is driven by `Effect`s emitted from ViewModels — no Navigation component needed.

| Screen | What you see |
|--------|-------------|
| **Category List** | All quiz categories with progress badges (Not Started / In Progress / Completed) |
| **Network Loader** | Animated dots while questions are fetched from the API |
| **Quiz** | Progress bar, question card, letter-badged options (A–D), answer feedback banner, streak badge, skip button |
| **Results** | Score ring, count-up animation, stats (correct / skipped / best streak), performance badge |
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

> ⚠️ JDK 24+ causes Kotlin 2.0.x parser errors. Keep JDK 23 set in `gradle.properties`

---

## ✨ Features

- 🗂️ **Multi-category** — browse a list of quiz categories, each tracked individually in Room DB
- 📶 **Progress persistence** — per-category status (NOT_STARTED / IN_PROGRESS / COMPLETED) and last-answered question ID survive app restarts
- 🌐 **Real API** — categories and questions fetched from remote Gist via Retrofit + OkHttp; URLs defined in `build.gradle.kts` and accessed via `BuildConfig`
- 🔤 **Letter-badged option cards** (A–D) with animated correct / wrong reveal states
- 🔥 **Streak celebration** — streak ≥ 3 triggers a fire overlay, auto-dismissed by the ViewModel at 1.5 s; the question advances at 2 s; `allTimeLongestStreak` persists in the Room database across sessions
- ✅ **Instant answer feedback** — Correct / Not quite! banners shown after selecting an option
- ⏩ **Skip = instant advance** — skipping immediately moves to the next question with no delay and no answer reveal
- 📊 **Animated progress bar**
- ⏳ **Auto-advance** — moves to next question 2 s after answering; cancelled immediately on skip
- 🏆 **Performance badge** — 🏆 Gold (8+) / 🥈 Silver (5–7) / 🥉 Bronze (0–4)
- 🔄 **Review mode** — reopening a COMPLETED quiz re-enters it as IN_PROGRESS on first answer, preserving history
- 💾 **Deferred IN_PROGRESS write** — the DB row is created only when the user first selects an option or skips; opening a quiz and immediately backing out leaves no phantom row
- ⚠️ **Error screen** — full-screen banner with retry; shown on network failure or empty response
- 🛡️ **Config-change safe** — API is called only once per ViewModel lifetime; rotation / theme switch does not re-trigger the network call
- 🔁 **In-memory category cache** — categories loaded once per app session; empty responses are not cached (retried on next access)

---

## 🛠️ Tech Stack

| Library | Version | Role |
|---|---|---|
| **Kotlin** | 2.0.21 | Language |
| **Jetpack Compose BOM** | 2024.09.03 | UI toolkit |
| **Material 3** | via BOM | Design system |
| **Dagger 2** | 2.51.1 | DI (no Hilt) |
| **Kotlin Coroutines** | 1.8.1 | Async / `viewModelScope` |
| **Retrofit** | 2.11.0 | Type-safe HTTP client for categories |
| **OkHttp** | 4.12.0 | Raw HTTP client for question JSONs + engine for Retrofit |
| **KotlinX Serialization JSON** | 1.7.3 | JSON ↔ data class conversion |
| **Room** | 2.6.1 | Local SQLite persistence (quiz progress) |
| **JUnit 4** | 4.13.2 | Unit testing |
| **MockK** | 1.13.12 | Kotlin-idiomatic mocking |
| **Turbine** | 1.1.0 | Flow / SharedFlow / StateFlow testing |
| **Min / Target SDK** | 28 / 35 | Android 9 → 15 |

> **Retrofit vs OkHttp** — complementary, not alternatives. Retrofit provides a type-safe Kotlin interface + coroutine support + serialization. OkHttp is the underlying HTTP engine handling raw connections, timeouts, and pooling. Retrofit always delegates to OkHttp; an explicit `OkHttpClient` gives fine-grained control over timeouts and interceptors.

---

## 🏗️ Architecture

**MVI + Domain-Driven Design** across 3 Gradle modules:

```
app/          ← Activity, ViewModelFactory, AppComponent (Dagger root)
data/         ← DTOs, Retrofit/OkHttp API services, Room DB, repository impls, NetworkModule
feature/quiz/ ← Compose screens (dumb), ViewModels, domain state, app services, DI modules
```

### MVI Contract

```
User Action
    │
    ▼  Event (sealed interface)
ViewModel.handleEvent()
    │
    ├──▶ _uiState: StateFlow<UiState>   ← pure data (questions, streaks, counts)
    │
    └──▶ _effects: SharedFlow<Effect>   ← navigation + one-shot signals (replay = 1)
```

Two ViewModels follow this contract:

| ViewModel | Events | Effects |
|---|---|---|
| `CategoryListViewModel` | `InitialLoad`, `Retry`, `CategorySelected` | `ShowLoader`, `ShowError`, `ShowList`, `NavigateToQuiz` |
| `QuizViewModel` | `InitialLoad`, `OptionSelected`, `SkipQuestion`, `FinishModule`, `NavigateBack`, `RetryApiCall` | `ShowLoader`, `NavigateToQuiz`, `NavigateToResults`, `ShowError`, `FinishModule`, `NavigateBack` |

### Module Graph

```
app  ──▶  feature:quiz  ──▶  data
app  ──▶  data
```

`Retrofit`, `OkHttpClient`, and `KotlinX JSON` are declared as `api(...)` in `:data` so Dagger's kapt in `:app` can resolve the types when building the component graph.

---

## 🔥 Streak System

- Streak badge (🔥 + count) always visible on the quiz screen
- Streak increments on each correct answer; resets to **0** on a wrong answer
- Skipping does **not** affect the streak
- **Streak ≥ 3** → fire overlay, auto-dismissed by ViewModel at 1.5 s; question advances at 2 s
- Celebration re-fires on every correct answer while streak stays ≥ 3
- `allTimeLongestStreak` is persisted in Room (`CategoryProgressEntity`) and survives app restarts

---

## 💾 Progress Persistence (Room DB)

**Schema** — `category_progress` table (`CategoryProgressEntity`, DB version 4):

| Column | Type | Description |
|---|---|---|
| `categoryId` | `String`  | Unique category identifier |
| `status` | `String` | `"NOT_STARTED"` / `"IN_PROGRESS"` / `"COMPLETED"` |
| `lastQuestionId` | `Int?` | Resume point; `null` on fresh start or after completion |
| `currentStreak` | `Int` | In-session streak (resets each new play) |
| `allTimeLongestStreak` | `Int` | Best streak ever across all sessions |

**Key behaviours:**
- **Deferred IN_PROGRESS write** — `saveProgress(IN_PROGRESS)` is only called when the user first selects an option or skips; backing out immediately leaves no DB row
- **Review mode** — reopening a COMPLETED quiz re-creates the row as IN_PROGRESS on the first answer (`wasCompleted` flag)
- **`updateStreaks`** — targeted SQL update preserves `lastQuestionId`; `upsert` preserves existing `allTimeLongestStreak` before overwriting
- **`updateLastQuestionId`** — called on NavigateBack so the user can resume from where they left off

---

## 🌐 API & Networking

| Detail | Value |
|---|---|
| **Categories endpoint** | `QUIZ_CATEGORY_URL` in `app/build.gradle.kts` → `BuildConfig.QUIZ_CATEGORY_URL` (Retrofit) |
| **Questions endpoint** | Per-category URL stored in the category object (OkHttp raw call) |
| **Format** | JSON array of category / question objects |
| **Timeouts** | 60 s connect / read / write (set on `OkHttpClient`) |
| **Error handling** | Any exception or empty list → `Effect.ShowError` → full-screen error banner |
| **Category cache** | In-memory `@Volatile` field in `CategoryAppService`; empty result not cached |

---

## 🧩 Dependency Injection (Pure Dagger 2)

| Module | Provides |
|---|---|
| `NetworkModule` | `OkHttpClient`, `Retrofit`, `QuestionApiService` |
| `DataModule` | `QuestionRepository`, `CategoryProgressRepository`, `QuizDatabase` |
| `QuizModule` | `QuizService` (via `QuizAppService`), `CategoryService` (via `CategoryAppService`) |
| `ViewModelModule` | `QuizViewModelFactory`, `CategoryListViewModelFactory` (singletons) |
| `AppComponent` | `@Singleton` root; injects into `MainActivity` |

`MainActivity` receives factories via `@Inject`, calls `inject(this)` in `onCreate`, then passes each factory to `viewModel(factory = …)` inside `setContent`.

---

## 🗂️ Data Flow

### Category List
```
MainActivity (LaunchedEffect → Event.InitialLoad)
        │
        ▼
CategoryListViewModel
   └── CategoryAppService.loadCategories()        → cached in-memory after first call
   └── CategoryAppService.getAllProgress()         → Room DB snapshot
   └── merge into List<CategoryWithProgress>
   └── emit Effect.ShowList                        → CategoryListScreen rendered
```

### Quiz
```
CategoryListScreen (CategorySelected event)
        │
        ▼
QuizViewModel.loadQuestions(url)
   ├── emit Effect.ShowLoader                      → NetworkLoaderScreen shown
   └── viewModelScope.launch {
           QuizAppService.loadQuestions(url)
               └── QuestionRepository
                       └── QuestionApiService (OkHttp → remote URL → JSON)
                               └── mapped to List<Question>
           QuizAppService.getProgress(categoryId)  → resume point from Room
           on success → _uiState.update + emit NavigateToQuiz
           on empty / error → emit ShowError
       }

User selects option or skips
   └── First interaction only:
           QuizAppService.saveProgress(IN_PROGRESS) ← deferred write
   └── Subsequent interactions:
           QuizAppService.updateStreaks()
           QuizAppService.updateLastQuestionId()

User presses Back (NavigateBack)
   └── if quiz was never interacted with → no DB call
   └── otherwise → updateLastQuestionId() for resume point
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

#### 150+ JVM unit tests across both the `:feature:quiz` and `:data` modules, using **MockK**, **Turbine**, and `StandardTestDispatcher` / `UnconfinedTestDispatcher`.