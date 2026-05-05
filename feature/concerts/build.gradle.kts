plugins {
    alias(libs.plugins.musicrecognizer.android.feature)
    alias(libs.plugins.musicrecognizer.android.library.compose)
}
android {
    namespace = "com.mrsep.musicrecognizer.feature.concerts"
}
dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.ui)
    implementation(projects.core.strings)
    implementation(libs.coil.compose)
}
