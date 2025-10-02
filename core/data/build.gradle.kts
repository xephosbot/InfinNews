import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.ksp)
}

val localPropertiesFile = project.file("$rootDir/local.properties")

val localProperties = Properties()
localProperties.load(FileInputStream(localPropertiesFile))

android {
    namespace = "com.xbot.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        buildConfigField("String", "API_KEY", localProperties["API_KEY"] as String)
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }
}

afterEvaluate {
    project.tasks.withType<Test> {
        systemProperties["robolectric.offline"] = "true"
        systemProperties["robolectric.dependency.dir"] = project.rootDir.path + "/robolectric-deps/"
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
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)

    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit)
    testImplementation(libs.okhttp.mockwebserver)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.test.ext)
}