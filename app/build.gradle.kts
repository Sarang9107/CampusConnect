
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.campusconnect"
    compileSdk = 36

    val localProperties by lazy {
        Properties().apply {
            val localPropertiesFile = project.rootProject.file("local.properties")
            if (localPropertiesFile.exists()) {
                load(localPropertiesFile.inputStream())
            }
        }
    }

    defaultConfig {
        applicationId = "com.example.campusconnect"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Expose GEMINI_API_KEY from local.properties
        // The key will be an empty string if not found in local.properties
        buildConfigField("String", "GEMINI_API_KEY", "\"${localProperties.getProperty("GEMINI_API_KEY") ?: ""}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true // Enable BuildConfig generation
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.12"
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3) // Kept one instance
    implementation("androidx.compose.material:material-icons-core:1.6.8") // Kept one instance
    implementation("androidx.compose.material:material-icons-extended:1.6.8") // Kept one instance
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
    implementation ("com.google.firebase:firebase-auth-ktx")
    implementation ("com.google.firebase:firebase-firestore-ktx")
    implementation ("com.google.firebase:firebase-messaging-ktx")
    testImplementation(libs.junit)
    // implementation(libs.androidx.material3) // Duplicate removed
    // implementation("androidx.compose.material:material-icons-core:1.6.8") // Duplicate removed
    // implementation("androidx.compose.material:material-icons-extended:1.6.8") // Duplicate removed
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("androidx.activity:activity-compose:1.8.2") // This is okay, different from activity.compose
    implementation("androidx.compose.ui:ui:1.6.7") // This might be redundant if using compose.bom and libs.androidx.ui, but let\'s keep for now if versions align
    // implementation("androidx.compose.material:material:1.6.7") // Removed older Material 2 dependency
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.7") // This might be redundant if using compose.bom and libs.androidx.ui.tooling.preview
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.1") // This might be redundant if using compose.bom and libs.androidx.lifecycle.runtime.ktx
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Ensure this aligns with the other BOM
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
