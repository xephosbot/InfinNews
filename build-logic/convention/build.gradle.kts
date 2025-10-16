import org.gradle.initialization.DependenciesAccessors
import org.gradle.kotlin.dsl.support.serviceOf
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.xbot.convention.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
    compileOnly(libs.compose.gradle.plugin)
    compileOnly(libs.detekt.gradle.plugin)
    // The line below allow us to access the libs from version catalog directly in plugins
    gradle.serviceOf<DependenciesAccessors>().classes.asFiles.forEach {
        compileOnly(files(it.absolutePath))
    }
}

tasks {
    validatePlugins {
        enableStricterValidation = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = libs.plugins.infinnews.android.application.get().pluginId
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.infinnews.android.library.get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = libs.plugins.infinnews.compose.get().pluginId
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("androidRoom") {
            id = libs.plugins.infinnews.room.get().pluginId
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register("detekt") {
            id = libs.plugins.infinnews.detekt.get().pluginId
            implementationClass = "DetektConventionPlugin"
        }
    }
}