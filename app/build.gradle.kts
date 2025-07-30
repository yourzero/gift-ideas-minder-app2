plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googleDaggerHilt)
    alias(libs.plugins.kotlinKapt)
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20"
    //id("dev.tonholo.s2c") version "2.1.2"  // ← use this ID and the latest version :contentReference[oaicite:0]{index=0}
    alias(libs.plugins.ksp) // Add this
}

android {
    namespace = "com.giftideaminder"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.giftideaminder"
        //minSdk = 24
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true // Add this
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    //implementation("androidx.compose.material:material-icons-core:1.6.8")
    implementation(libs.navigation.compose)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler) // Change from kapt to ksp
    //implementation(libs.mlkitVisionText)
    implementation("com.google.mlkit:text-recognition:16.0.1")
    implementation("com.opencsv:opencsv:5.11.2")
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.gson)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    // In your module-level build.gradle.kts:
    implementation("androidx.compose.material:material-icons-extended:1.4.3")
    implementation("androidx.compose.material3:material3:1.1.0")
    implementation("io.coil-kt:coil-compose:2.4.0")
}

//svgToCompose {
//    processor {
//        val projectIcons by creating {
//            from(layout.projectDirectory.dir("src/main/svg"))
//            destinationPackage("com.threekidsinatrenchcoat.ui.icons")
//            icons {
//                theme("com.example.app.ui.theme.AppTheme")
//            }
//            // Additional configurations...
//        }
//    }
//}
////s2c {
////    inputDirectory.set(file("src/main/svg"))
////    outputPackage.set("com.threekidsinatrenchcoat.ui.icons")
////}