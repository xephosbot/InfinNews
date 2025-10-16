plugins {
    alias(libs.plugins.infinnews.android.library)
    alias(libs.plugins.infinnews.compose)
}

android {

}

dependencies {
    api(projects.core.domain)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.material3)
    implementation(libs.kotlinx.datetime)
    implementation(libs.coil.compose)
    implementation(libs.androidx.paging.compose)
}