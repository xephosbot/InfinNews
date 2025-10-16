plugins {
    alias(libs.plugins.infinnews.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {

}

dependencies {
    api(projects.core.common)
    implementation(libs.kotlinx.serialization)
}