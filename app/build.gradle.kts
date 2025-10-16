plugins {
    alias(libs.plugins.infinnews.android.application)
    alias(libs.plugins.infinnews.compose)
}

android {
    defaultConfig {
        applicationId = "com.xbot.infinnews"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = true
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.designSystem)
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.feature.list.impl)
    implementation(projects.feature.details.impl)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.android)
    implementation(libs.coil.okhttp)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
}