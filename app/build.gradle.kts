plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "one.owo.v2.checkin"
    compileSdk = 34

    defaultConfig {
        applicationId = "one.owo.v2.checkin"
        minSdk = 29
        targetSdk = 29
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.gson)
    implementation(libs.poi)
    implementation(libs.poi.ooxml)
    implementation(libs.icu4j)
    implementation(libs.icu4j.charset)
    implementation(libs.icu4j.localespi)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}