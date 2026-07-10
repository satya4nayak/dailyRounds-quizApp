plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.assignment.mcqquiz.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 28
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Serialization (QuestionDto)
    implementation(libs.kotlinx.serialization.json)

    // Dagger — pure, no Hilt
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
}

