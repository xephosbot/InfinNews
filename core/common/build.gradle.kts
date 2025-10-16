plugins {
    alias(libs.plugins.infinnews.android.library)
    alias(libs.plugins.infinnews.compose)
}

android {

}

dependencies {
    api(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.koin.compose)
}