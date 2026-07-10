package com.assignment.mcqquiz.data.source;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \u00072\u00020\u0001:\u0001\u0007B\u0007\b\u0007\u00a2\u0006\u0002\u0010\u0002J\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u0086@\u00a2\u0006\u0002\u0010\u0006\u00a8\u0006\b"}, d2 = {"Lcom/assignment/mcqquiz/data/source/QuizApiService;", "", "()V", "fetchQuestions", "", "Lcom/assignment/mcqquiz/data/dto/QuestionDto;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Companion", "data_debug"})
public final class QuizApiService {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String RAW_JSON = "[\n  {\"id\":1,\"question\":\"What hidden feature do recent Android versions reveal when you tap the version number multiple times in Settings?\",\"options\":[\"Flappy Bird-style game\",\"Virtual pet\",\"Hidden performance menu\",\"System UI tuner\"],\"correctOptionIndex\":0},\n  {\"id\":2,\"question\":\"If you were to implement 'shake to undo' in your Android app, what's the biggest technical challenge you'd face?\",\"options\":[\"Detecting accidental shakes\",\"Battery drain due to sensors\",\"Android doesn't allow motion APIs\",\"Undo logic is illegal on Android\"],\"correctOptionIndex\":0},\n  {\"id\":3,\"question\":\"Which Android system permission is needed to draw a floating game dashboard overlay on top of all apps?\",\"options\":[\"SYSTEM_ALERT_WINDOW\",\"ACCESS_OVERLAY_UI\",\"FOREGROUND_SERVICE\",\"BIND_NOTIFICATION_LISTENER_SERVICE\"],\"correctOptionIndex\":0},\n  {\"id\":4,\"question\":\"If your app's CPU usage is constantly above 90%, what's the most likely user-facing symptom?\",\"options\":[\"Janky animations and slow UI\",\"More Google Play reviews\",\"Better battery life\",\"High-resolution screenshots\"],\"correctOptionIndex\":0},\n  {\"id\":5,\"question\":\"You added a hidden gesture that unlocks a secret screen in your app. How should users discover it?\",\"options\":[\"Let influencers leak it\",\"Add subtle hints in UI\",\"Keep it undocumented forever\",\"Send a notification at midnight\"],\"correctOptionIndex\":1},\n  {\"id\":6,\"question\":\"Which gesture is most consistent across both Android and iOS notification systems?\",\"options\":[\"Swipe to dismiss\",\"Double tap to expand\",\"Pinch to close\",\"Shake to open\"],\"correctOptionIndex\":0},\n  {\"id\":7,\"question\":\"You're building a cross-platform mobile app. What's the best strategy to handle platform-specific UX features?\",\"options\":[\"Respect native behaviors per platform\",\"Force one design for all\",\"Add toggles for users to choose\",\"Ignore iOS, it's annoying\"],\"correctOptionIndex\":0},\n  {\"id\":8,\"question\":\"Which approach best improves cold start time of your Android app?\",\"options\":[\"Move logic to background thread after splash\",\"Add more splash screen animations\",\"Force dark mode\",\"Use larger drawables\"],\"correctOptionIndex\":0},\n  {\"id\":9,\"question\":\"If a feature relies heavily on sensors (gyroscope, accelerometer), what's one accessibility concern to keep in mind?\",\"options\":[\"Not all users can perform physical gestures\",\"Sensors drain Wi-Fi speed\",\"Apps become less colorful\",\"It won't run in dark mode\"],\"correctOptionIndex\":0},\n  {\"id\":10,\"question\":\"Which Android system option helps developers visualize screen redraws and UI jank during testing?\",\"options\":[\"Show GPU overdraw\",\"System WebView debug\",\"Location simulator\",\"Enable dark theme\"],\"correctOptionIndex\":0}\n]";
    @org.jetbrains.annotations.NotNull()
    public static final com.assignment.mcqquiz.data.source.QuizApiService.Companion Companion = null;
    
    @javax.inject.Inject()
    public QuizApiService() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object fetchQuestions(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.assignment.mcqquiz.data.dto.QuestionDto>> $completion) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/assignment/mcqquiz/data/source/QuizApiService$Companion;", "", "()V", "RAW_JSON", "", "data_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}