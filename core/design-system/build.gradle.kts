import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.xbot.designsystem"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
            freeCompilerArgs.add("-Xcontext-receivers")
        }
    }

    buildFeatures {
        compose = true
    }
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    metricsDestination = layout.buildDirectory.dir("compose_compiler")

    stabilityConfigurationFile = rootProject.layout.projectDirectory.file("stability_config.conf")
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    api(projects.core.domain)
    implementation(libs.kotlinx.datetime)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.material3)
    api(libs.shimmer.compose)
    implementation(libs.coil.compose)
    implementation(libs.androidx.paging.compose)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.compose.ui.tooling)
}