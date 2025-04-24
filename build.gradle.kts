plugins {
    id("com.android.application").version("8.9.1")
    kotlin("android").version("2.1.20")
    id("org.jetbrains.kotlin.plugin.compose").version("2.1.20")
    id("org.jlleitschuh.gradle.ktlint").version("12.2.0")
}

android {
    namespace = "dev.bojidar_bg.memory"
    compileSdk = 36
    buildToolsVersion = "35.0.1"
    defaultConfig {
        applicationId = "dev.bojidar_bg.memory"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.0.1"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.compose.ui:ui:1.7.8")
    implementation("androidx.compose.ui:ui-tooling:1.7.8")
    implementation("androidx.compose.foundation:foundation:1.7.8")
    implementation("androidx.compose.material:material:1.7.8")
    implementation("androidx.activity:activity-compose:1.10.1")
}
