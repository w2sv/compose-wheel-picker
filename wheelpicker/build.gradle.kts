plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose.compiler)
    `maven-publish`
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

android {
    namespace = "com.w2sv.wheelpicker"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
    }
    buildFeatures {
        compose = true
        buildConfig = false
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

// https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-compiler.html#compose-compiler-options-dsl
composeCompiler {
    includeSourceInformation = true
//    stabilityConfigurationFile.set(rootProject.file("compose_compiler_config.conf"))
    metricsDestination.set(project.layout.buildDirectory.dir("compose_compiler"))
    reportsDestination.set(project.layout.buildDirectory.dir("compose_compiler"))
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.w2sv"
            artifactId = "wheelpicker"
            version = version.toString()
            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.compose.foundation)
    implementation(libs.slimber)
    implementation(libs.androidx.compose.material3)
    lintChecks(libs.compose.lint.checks)
}