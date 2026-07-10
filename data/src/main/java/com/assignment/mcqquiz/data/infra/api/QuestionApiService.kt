package com.assignment.mcqquiz.data.infra.api

import com.assignment.mcqquiz.data.infra.dto.QuestionDto
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * Mock API service that simulates a remote quiz data source.
 * The 1-second delay simulates network latency.
 * Replace with a Retrofit/Ktor client for production use.
 */
class QuestionApiService @Inject constructor() : QuestionApiClient {

    override suspend fun fetchQuestions(): List<QuestionDto> {
        delay(1_000L)
        return Json.decodeFromString(QUESTIONS_JSON)
    }

    companion object {
        private const val QUESTIONS_JSON = """[
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
]"""
    }
}

