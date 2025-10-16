package com.xbot.convetion

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        dependencies {
            implementation(platform(libs.androidx.compose.bom))
            androidTestImplementation(platform(libs.androidx.compose.bom))
            implementation(libs.androidx.compose.ui.tooling.preview)
            debugImplementation(libs.androidx.compose.ui.tooling.asProvider())
        }

        extensions.configure<ComposeCompilerGradlePluginExtension> {
            metricsDestination.set(layout.buildDirectory.dir("compose_compiler"))
            reportsDestination.set(layout.buildDirectory.dir("compose_compiler"))
            stabilityConfigurationFile.set(rootProject.layout.projectDirectory.file("stability_config.conf"))
        }
    }
}