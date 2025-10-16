plugins {
    alias(libs.plugins.infinnews.android.library)
}

android {

}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)
    implementation(libs.koin.core)
    implementation(libs.androidx.paging.core)
}