plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.marcoscherzer.msimplespeechbackend"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.marcoscherzer.msimplespeechbackend"
        minSdk = 34
        targetSdk = 36
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
}

dependencies {
    implementation(project(":MGridBuilder_AndroidVersion"))
    // AndroidX & Material
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(files("Z:\\MarcoScherzer-Projects\\msimplespeechbackend\\app\\libs\\nanohttpd-2.3.1.jar"))
    // Test Libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}