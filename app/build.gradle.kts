plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "2.0.0"
}

android {
    namespace = "com.example.medicineapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.medicineapplication"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    // Core Android libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity.ktx.v172)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)

    implementation("com.google.mlkit:text-recognition:16.0.0")
    implementation ("com.google.mlkit:image-labeling:17.0.0")

    // uCrop for image cropping
    implementation(libs.poi)
    implementation(libs.poi.ooxml)

    // Kotlin Standard Library
    implementation(libs.kotlin.stdlib)

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}