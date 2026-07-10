# INSTRUCTIONS.md: R0 MCQ Quiz Android App

> Read this file completely before writing a single line of code.
> Every decision is already made here. Do not assume, do not deviate.

***

## 1. Project Overview

Build a polished Android MCQ Quiz app in **Kotlin + Jetpack Compose**.

* 10 questions fetched from a mocked network layer (hardcoded JSON, simulated 1 s delay)
* Splash -> Quiz -> Results flow managed via a sealed class screen state in a single Activity
* MVVM + DDD architecture enforced via **multi-module Gradle**
* DI via **Pure Dagger 2 (No Hilt)**
* No unit tests (to be added manually later)

***

## 2. Tech Stack & Versions

| Tool / Library | Version | Purpose |
| :--- | :--- | :--- |
| Kotlin | 2.0.x | Primary language |
| Jetpack Compose BOM | 2024.09.xx | UI toolkit |
| Dagger 2 | 2.5x | Dependency Injection |
| Kotlin Coroutines | 1.8.x | Async / concurrency |
| Kotlinx Serialization | 1.7.x | JSON parsing |
| Lottie Compose | 6.x | Streak fire animation |
| Material3 | via Compose BOM | Design system |
| Min SDK | 28 (Android 9) | |
| Target SDK | 35 | |
| Compile SDK | 35 | |
| AGP | 8.x | Android Gradle Plugin |

> Use Gradle version catalog (`libs.versions.toml`) for all dependency declarations.

***

## 3. Module Structure

```text
root/
├── app/                        <- Android Application module
├── domain/                     <- Pure Kotlin module (no Android deps)
├── data/                       <- Android library module
└── feature/
    └── quiz/                   <- Android library module
```

### Module dependency graph

```text
app  --depends on-->  feature:quiz
app  --depends on-->  data
feature:quiz  --depends on-->  domain
data  --depends on-->  domain
```

**Hard rules:**
* `domain` has **zero** Android or third-party dependencies (pure Kotlin only).
* `data` depends on `domain` only. DTOs and raw JSON types never leave `data`.
* `feature:quiz` depends on `domain` only. It never imports anything from `data`.
* `app` wires everything together: Dagger components/modules, `MainActivity`, `QuizAppService`.
* The only inter-layer currency between modules is **domain models**.

***

## 4. Package Structure (per module)

```text
# domain/
com.r0.quiz.domain/
├── model/
│   ├── Question.kt             <- Domain model
│   └── QuizResult.kt          <- Domain model
└── repository/
    └── QuestionRepository.kt  <- Port (interface)

# data/
com.r0.quiz.data/
├── dto/
│   └── QuestionDto.kt         <- Raw JSON shape (stays inside :data)
├── mapper/
│   └── QuestionMapper.kt      <- QuestionDto to Question (domain)
├── source/
│   └── QuizApiService.kt      <- Fake network source (hardcoded JSON + delay)
├── repository/
│   └── QuestionRepositoryImpl.kt <- Implements domain port
└── di/
    └── DataModule.kt          <- Dagger module

# feature/quiz/
com.r0.quiz.feature.quiz/
├── ui/
│   ├── screen/
│   │   ├── SplashScreen.kt
│   │   ├── QuizScreen.kt
│   │   └── ResultScreen.kt
│   └── component/
│       ├── OptionCard.kt
│       ├── ProgressBar.kt
│       ├── StreakBadge.kt
│       └── FireOverlay.kt
├── viewmodel/
│   └── QuizViewModel.kt
└── state/
    ├── QuizUiState.kt
    └── AppScreen.kt           <- Sealed class for screen navigation

# app/
com.r0.quiz/
├── MainActivity.kt
├── QuizApplication.kt
├── service/
│   └── QuizAppService.kt      <- Thin orchestrator: wires repo to ViewModel
└── di/
    ├── AppModule.kt           <- Dagger module
    └── AppComponent.kt        <- Dagger component (wiring it all)
```

***

## 5. Domain Layer (`domain/`)

### 5.1 Domain Models

```kotlin
// domain/model/Question.kt
data class Question(
    val id: Int,
    val text: String,
    val options: List<String>,          // always 4 items
    val correctOptionIndex: Int
)

// domain/model/QuizResult.kt
data class QuizResult(
    val totalQuestions: Int,
    val correctCount: Int,
    val skippedCount: Int,
    val longestStreak: Int
)
```

### 5.2 Repository Port

```kotlin
// domain/repository/QuestionRepository.kt
interface QuestionRepository {
    suspend fun getQuestions(): List<Question>
}
```

***

## 6. Data Layer (`data/`)

### 6.1 DTO

```kotlin
// data/dto/QuestionDto.kt
@Serializable
data class QuestionDto(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int
)
```

The raw JSON is the 10-question list from:
`https://gist.githubusercontent.com/dr-samrat/53846277a8fcb034e482906ccc0d12b2/raw`

Hardcode this exact JSON string as a `private const val` inside `QuizApiService`.

### 6.2 ApiService (Fake / Mocked)

```kotlin
// data/source/QuizApiService.kt
class QuizApiService {
    suspend fun fetchQuestions(): List<QuestionDto> {
        delay(1_000L)   // simulate network latency
        return Json.decodeFromString(RAW_JSON)
    }

    companion object {
        private const val RAW_JSON = """[ ... paste exact JSON here ... ]"""
    }
}
```

* Use `kotlinx.serialization` for parsing.
* `delay()` is a `kotlinx.coroutines` suspension. No threads, no `Thread.sleep`.

### 6.3 Mapper

```kotlin
// data/mapper/QuestionMapper.kt
fun QuestionDto.toDomain(): Question = Question(
    id = id,
    text = question,
    options = options,
    correctOptionIndex = correctOptionIndex
)
```

### 6.4 Repository Implementation

```kotlin
// data/repository/QuestionRepositoryImpl.kt
class QuestionRepositoryImpl @Inject constructor(
    private val apiService: QuizApiService
) : QuestionRepository {
    override suspend fun getQuestions(): List<Question> =
        apiService.fetchQuestions().map { it.toDomain() }
}
```

### 6.5 Dagger Data Module

```kotlin
// data/di/DataModule.kt
@Module
class DataModule {

    @Provides
    @Singleton
    fun provideQuizApiService(): QuizApiService = QuizApiService()

    @Provides
    @Singleton
    fun provideQuestionRepository(
        apiService: QuizApiService
    ): QuestionRepository = QuestionRepositoryImpl(apiService)
}
```

***

## 7. App Layer (`app/`)

### 7.1 QuizAppService (Thin Orchestrator)

```kotlin
// app/service/QuizAppService.kt
class QuizAppService @Inject constructor(
    private val repository: QuestionRepository
) {
    suspend fun loadQuestions(): List<Question> = repository.getQuestions()
}
```

That is all `QuizAppService` does. No streak logic, no state, no timer here.

### 7.2 Dagger App Module & Component

```kotlin
// app/di/AppModule.kt
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideQuizAppService(
        repository: QuestionRepository
    ): QuizAppService = QuizAppService(repository)
}

// app/di/AppComponent.kt
@Singleton
@Component(modules = [AppModule::class, DataModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
}
```

### 7.3 QuizApplication

```kotlin
class QuizApplication : Application() {
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .dataModule(DataModule())
            .appModule(AppModule())
            .build()
    }
}
```

### 7.4 MainActivity

`MainActivity` hosts the single `setContent { }` block. We inject a `Provider<QuizViewModel>` to instantiate the ViewModel without heavy multibinding boilerplate, then pass it down to `QuizRoot`.

```kotlin
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var viewModelProvider: Provider<QuizViewModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as QuizApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        setContent {
            // Simple factory to provide our injected ViewModel to Compose
            val factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return viewModelProvider.get() as T
                }
            }
            val viewModel: QuizViewModel = viewModel(factory = factory)

            QuizAppTheme {
                QuizRoot(viewModel = viewModel)
            }
        }
    }
}
```

***

## 8. Feature Layer (`feature/quiz/`)

### 8.1 Screen State (Navigation)

```kotlin
// feature/quiz/state/AppScreen.kt
sealed interface AppScreen {
    data object Splash  : AppScreen
    data object Quiz    : AppScreen
    data object Results : AppScreen
}
```

No Jetpack Navigation. The ViewModel exposes a `StateFlow<AppScreen>` and the root composable switches on it.

### 8.2 Quiz UI State

```kotlin
// feature/quiz/state/QuizUiState.kt
data class QuizUiState(
    val questions: List<Question>        = emptyList(),
    val currentIndex: Int                = 0,
    val selectedOptionIndex: Int?        = null,   // null = unanswered
    val isAnswerRevealed: Boolean        = false,
    val currentStreak: Int               = 0,
    val longestStreak: Int               = 0,
    val showStreakCelebration: Boolean    = false,  // triggers fire overlay
    val correctCount: Int                = 0,
    val skippedCount: Int                = 0,
    val screen: AppScreen                = AppScreen.Splash,
    val isLoading: Boolean               = true
)
```

### 8.3 QuizViewModel

`QuizViewModel` is the **single source of truth**. It owns all quiz logic. Notice it just uses a standard `@Inject constructor`.

```kotlin
class QuizViewModel @Inject constructor(
    private val appService: QuizAppService
) : ViewModel() {
    // ... ViewModel logic ...
}
```

**Responsibilities:**

1.  **Load questions**: call `QuizAppService.loadQuestions()` in `init {}`, set `isLoading = false`, transition to `AppScreen.Quiz` after load.
2.  **Answer selection**: when the user taps an option:
    * Set `selectedOptionIndex` and `isAnswerRevealed = true`.
    * Evaluate correctness.
    * If correct: increment `currentStreak`; update `longestStreak` if exceeded; if `currentStreak` reaches a **multiple of 3** (3, 6, 9) set `showStreakCelebration = true`.
    * If wrong: reset `currentStreak = 0`.
    * Launch a coroutine with `delay(2_000L)` then call `advanceQuestion()`.
3.  **Skip**: call `advanceQuestion()` immediately, increment `skippedCount`. Streak is NOT reset on skip; it simply does not increment.
4.  **Advance question** (`advanceQuestion()`):
    * Clear `selectedOptionIndex`, `isAnswerRevealed`, `showStreakCelebration`.
    * If `currentIndex + 1 < questions.size` -> increment `currentIndex`.
    * Else -> set `screen = AppScreen.Results`.
5.  **Restart**: reset `QuizUiState` entirely back to defaults, reload questions.

**Key rule:** Cancel any pending auto-advance coroutine when the user hits Skip before the 2 s delay fires. Use a `Job` variable:

```kotlin
private var autoAdvanceJob: Job? = null

fun onOptionSelected(index: Int) {
    // ... update state ...
    autoAdvanceJob?.cancel()
    autoAdvanceJob = viewModelScope.launch {
        delay(2_000L)
        advanceQuestion()
    }
}

fun onSkip() {
    autoAdvanceJob?.cancel()
    advanceQuestion()  // skippedCount incremented here
}
```

***

## 9. UI Screens

### 9.1 QuizRoot (top-level composable)

```kotlin
@Composable
fun QuizRoot(viewModel: QuizViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when (state.screen) {
        AppScreen.Splash  -> SplashScreen()
        AppScreen.Quiz    -> QuizScreen(state, viewModel::onOptionSelected, viewModel::onSkip)
        AppScreen.Results -> ResultScreen(state, viewModel::onRestart)
    }
}
```

### 9.2 SplashScreen

* Full-screen centered layout.
* App name/logo + `CircularProgressIndicator`.
* Automatically replaced by QuizScreen once `isLoading = false` (driven by `state.screen` switching to `AppScreen.Quiz`).
* Minimum visible time: the 1 s fake delay is enough; no extra artificial delay.

### 9.3 QuizScreen

Layout (top to bottom):

1.  **Progress row**: `LinearProgressIndicator` showing `(currentIndex + 1) / totalQuestions`. Animate with `animateFloatAsState`.
2.  **Question counter**: "Question X of 10".
3.  **Streak badge row**: always visible; dims when streak < 3, glows when streak >= 3. See Section 10.
4.  **Question text card**: large, readable, vertically centered.
5.  **Four `OptionCard` composables**: see below.
6.  **Skip button**: bottom of screen, text button.

**OptionCard states:**

| State | Visual |
| :--- | :--- |
| Unanswered | Neutral surface color, no highlight |
| Selected & Correct | Green background + checkmark icon |
| Selected & Wrong | Red background + X icon |
| Unselected & Correct (revealed) | Green outline (shows right answer) |
| Unselected & Wrong | No change |

All option color transitions use `animateColorAsState` with a short `tween(300ms)`.

After the answer is revealed, all option cards become **non-clickable** (disable `onClick`).

### 9.4 ResultScreen

Layout:

```text
[Congratulations! / Quiz Complete]
[Score ring or progress visual]
Correct Answers:   X / 10
Skipped:           X
Longest Streak:    X 🔥
[Restart Quiz Button]
```

* Animate the score number counting up from 0 to `correctCount` using `LaunchedEffect` + incrementing counter with `delay(50ms)` per step.
* `Restart Quiz` button calls `viewModel.onRestart()`.

***

## 10. Streak Celebration (Combined Fire + Overlay)

When `showStreakCelebration == true` in `QuizUiState`:

**Part A: StreakBadge (persistent, top of QuizScreen):**
* Shows a fire emoji 🔥 + current streak count.
* When `currentStreak >= 3`: badge pulses with `infiniteTransition` scale animation + glows using `Modifier.shadow()` or a custom glow effect.
* When `currentStreak < 3`: badge is greyed out, no animation.

**Part B: FireOverlay (one-shot celebration):**
* A full-screen `Box` overlay with `alpha = 0.85f` background drawn over the QuizScreen.
* Inside: a **Lottie animation** (use a fire/confetti JSON from LottieFiles. Pick a free one and bundle it in `feature/quiz/src/main/assets/`).
* Below the Lottie: animated text `"You're on fire! 🔥"` fades in with `AnimatedVisibility`.
* The overlay **auto-dismisses after 1.5 seconds** via a `LaunchedEffect(showStreakCelebration)` inside the composable. It does **not** block the auto-advance timer (both run concurrently; the overlay just disappears visually).
* Dismiss early on any tap.

```kotlin
// Trigger logic inside QuizScreen
if (state.showStreakCelebration) {
    FireOverlay(
        streakCount = state.currentStreak,
        onDismiss = viewModel::onStreakCelebrationDismissed
    )
}
```

Add `onStreakCelebrationDismissed()` to ViewModel which simply sets `showStreakCelebration = false`.

***

## 11. Animations & Micro-interactions Summary

| Interaction | Implementation |
| :--- | :--- |
| Progress bar advance | `animateFloatAsState(tween(400ms))` |
| Option color on answer reveal | `animateColorAsState(tween(300ms))` |
| Screen transitions | `AnimatedContent` with `fadeIn + slideInVertically` |
| Streak badge pulse | `infiniteTransition` scale 1f to 1.08f to 1f |
| Fire overlay entrance | `AnimatedVisibility(scaleIn + fadeIn)` |
| Results score count-up | `LaunchedEffect` counter with `delay(50ms)` per digit |
| OptionCard tap | `Modifier.clickable` with `indication = rememberRipple()` |

***

## 12. Theming & Design System

Define in `feature/quiz/ui/theme/`:

```text
Theme.kt       <- MaterialTheme wrapper, dark theme preferred
Color.kt       <- color palette
Type.kt        <- typography scale
```

**Color palette (dark-first, as per design reference):**

| Token | Value |
| :--- | :--- |
| `Background` | `#121212` |
| `Surface` | `#1E1E1E` |
| `Primary` | `#6C63FF` |
| `CorrectGreen` | `#4CAF50` |
| `WrongRed` | `#F44336` |
| `TextPrimary` | `#FFFFFF` |
| `TextSecondary` | `#B0B0B0` |
| `StreakActive` | `#FF6D00` |
| `StreakInactive` | `#3A3A3A` |

Typography: Use `Roboto` (default). Question text: `titleLarge`. Options: `bodyLarge`. Labels: `bodyMedium`.

***

## 13. File-by-File Build Order

Follow this exact order to avoid compilation errors between modules:

1.  `domain/`: `Question.kt`, `QuizResult.kt`, `QuestionRepository.kt`
2.  `data/`: `QuestionDto.kt`, `QuizApiService.kt`, `QuestionMapper.kt`, `QuestionRepositoryImpl.kt`, `DataModule.kt`
3.  `app/`: `QuizAppService.kt`, `AppModule.kt`, `AppComponent.kt`, `QuizApplication.kt`, `MainActivity.kt`
4.  `feature/quiz/`: `AppScreen.kt`, `QuizUiState.kt`, `QuizViewModel.kt`, then screens bottom-up: `OptionCard.kt`, `StreakBadge.kt`, `FireOverlay.kt`, `ProgressBar.kt`, `SplashScreen.kt`, `QuizScreen.kt`, `ResultScreen.kt`, `QuizRoot.kt`
5.  Wire `AndroidManifest.xml` in `:app`: register `QuizApplication` and `MainActivity` with `windowSoftInputMode="adjustResize"`.

***

## 14. Gradle Setup Notes

* Root `settings.gradle.kts` must include all modules:
    ```kotlin
    include(":app", ":domain", ":data", ":feature:quiz")
    ```
* `domain/build.gradle.kts`: `kotlin("jvm")` plugin only, no `com.android.library`.
* `data/build.gradle.kts`: `com.android.library` + `kotlin("plugin.serialization")` + `kotlin("kapt")` + Dagger dependencies.
* `feature/quiz/build.gradle.kts`: `com.android.library` + Compose enabled + `kotlin("kapt")` + Dagger dependencies.
* `app/build.gradle.kts`: `com.android.application` + depends on all other modules + `kotlin("kapt")` + Dagger dependencies.
* Lottie asset goes in `feature/quiz/src/main/assets/streak_fire.json`.

**Dagger Dependencies for modules:**
```kotlin
implementation("com.google.dagger:dagger:2.5x")
kapt("com.google.dagger:dagger-compiler:2.5x")
```

***

## 15. Hardcoded JSON (paste exactly inside `QuizApiService`)

```json
[
  {"id":1,"question":"What hidden feature do recent Android versions reveal when you tap the version number multiple times in Settings?","options":["Flappy Bird-style game","Virtual pet","Hidden performance menu","System UI tuner"],"correctOptionIndex":0},
  {"id":2,"question":"If you were to implement 'shake to undo' in your Android app, what's the biggest technical challenge you'd face?","options":["Detecting accidental shakes","Battery drain due to sensors","Android doesn't allow motion APIs","Undo logic is illegal on Android"],"correctOptionIndex":0},
  {"id":3,"question":"Which Android system permission is needed to draw a floating game dashboard overlay on top of all apps?","options":["SYSTEM_ALERT_WINDOW","ACCESS_OVERLAY_UI","FOREGROUND_SERVICE","BIND_NOTIFICATION_LISTENER_SERVICE"],"correctOptionIndex":0},
  {"id":4,"question":"If your app's CPU usage is constantly above 90%, what's the most likely user-facing symptom?","options":["Janky animations and slow UI","More Google Play reviews","Better battery life","High-resolution screenshots"],"correctOptionIndex":0},
  {"id":5,"question":"You added a hidden gesture that unlocks a secret screen in your app. How should users discover it?","options":["Let influencers leak it","Add subtle hints in UI","Keep it undocumented forever","Send a notification at midnight"],"correctOptionIndex":1},
  {"id":6,"question":"Which gesture is most consistent across both Android and iOS notification systems?","options":["Swipe to dismiss","Double tap to expand","Pinch to close","Shake to open"],"correctOptionIndex":0},
  {"id":7,"question":"You're building a cross-platform mobile app. What's the best strategy to handle platform-specific UX features?","options":["Respect native behaviors per platform","Force one design for all","Add toggles for users to choose","Ignore iOS, it's annoying"],"correctOptionIndex":0},
  {"id":8,"question":"Which approach best improves cold start time of your Android app?","options":["Move logic to background thread after splash","Add more splash screen animations","Force dark mode","Use larger drawables"],"correctOptionIndex":0},
  {"id":9,"question":"If a feature relies heavily on sensors (gyroscope, accelerometer), what's one accessibility concern to keep in mind?","options":["Not all users can perform physical gestures","Sensors drain Wi-Fi speed","Apps become less colorful","It won't run in dark mode"],"correctOptionIndex":0},
  {"id":10,"question":"Which Android system option helps developers visualize screen redraws and UI jank during testing?","options":["Show GPU overdraw","System WebView debug","Location simulator","Enable dark theme"],"correctOptionIndex":0}
]
```

***

## 16. Decisions Made: Do Not Re-decide

| Decision | Choice |
| :--- | :--- |
| JSON source | Hardcoded in `QuizApiService`, 1 s delay |
| Module structure | Multi-module Gradle |
| Navigation | Sealed class `AppScreen` in single Activity |
| DI framework | Pure Dagger 2 (No Hilt) |
| JSON parser | `kotlinx.serialization` |
| Streak celebration | Lottie fire animation + full-screen overlay |
| AppService responsibility | Thin pass-through only (fetch + return) |
| Streak trigger threshold | Every multiple of 3 correct in a row |
| Skip effect on streak | No reset, no increment |
| Auto-advance after answer | 2 s delay, cancellable by Skip |
| Unit tests | None. Added manually later |
| Min SDK | 28 |