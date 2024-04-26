plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.sd.demo.compose.wheel_picker"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()
    defaultConfig {
        targetSdk = libs.versions.androidCompileSdk.get().toInt()
        minSdk = libs.versions.androidMinSdk.get().toInt()
        applicationId = "com.sd.demo.compose.wheel_picker"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

dependencies {
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.ripple)
    implementation(libs.androidx.compose.material.icons.core)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)

    implementation(libs.slimber)
    implementation(project(":wheelpicker"))
}