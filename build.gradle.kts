plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.googleDaggerHilt) apply false
    alias(libs.plugins.kotlinKapt) apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20" // Match your Kotlin version

}