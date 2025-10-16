plugins {
    alias(libs.plugins.infinnews.android.library)
    alias(libs.plugins.infinnews.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {

}

dependencies {
    implementation(projects.feature.list.api)
    implementation(projects.feature.details.api)
    implementation(projects.core.designSystem)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
    implementation(libs.koin.core)
    implementation(libs.koin.compose.viewmodel)
}