package com.xbot.convetion

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        namespace = path.toPackageName(replacements = mapOf("app" to "infinnews"))
        compileSdk = 36

        defaultConfig {
            minSdk = 24
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
            isCoreLibraryDesugaringEnabled = true
        }

        packaging {
            resources {
                excludes += "/META_INF/{AL2.0,LGPL2.1}"
            }
        }
    }

    configure<KotlinAndroidProjectExtension> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            freeCompilerArgs.add("-Xcontext-receivers")
        }
    }

    dependencies {
        coreLibraryDesugaring(libs.desugar.jdk.libs)
    }
}

private fun String.toPackageName(
    prefix: String = "com.xbot",
    replacements: Map<String, String> = emptyMap(),
): String {
    val parts = this.removePrefix(":").replace("-", "").split(":")
    val transformedParts = parts.map { part ->
        replacements[part] ?: part
    }
    return "$prefix." + transformedParts.joinToString(".")
}