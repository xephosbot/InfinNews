import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.infinnews.android.library)
    alias(libs.plugins.infinnews.room)
    alias(libs.plugins.kotlin.serialization)
}

android {
    defaultConfig {
        val key = gradleLocalProperties(rootDir, providers).getProperty("API_KEY")
        buildConfigField("String", "API_KEY", key)
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.domain)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization)
    implementation(libs.kotlinx.datetime)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization)
    implementation(libs.okhttp.logging)
    implementation(libs.androidx.paging.core)
    implementation(libs.androidx.room.paging)

    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit)
    testImplementation(libs.okhttp.mockwebserver)
    testImplementation(libs.androidx.paging.testing)
}