plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "cu.lenier.cashrpido"
    compileSdk = 35

    defaultConfig {
        applicationId = "cu.lenier.cashrpido"
        minSdk = 24
        targetSdk = 35
        versionCode = 40122435
        versionName = "1.5"

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.mpandroidchart)
    implementation(libs.work.runtime)
    implementation(libs.lottie)
    implementation (libs.update.checker)
    implementation (libs.gson)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}