plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
}

android {
    configurations {
        namespace = "com.shubham.alarmassignment"
        compileSdk = 35
        defaultConfig {
            applicationId = "com.shubham.alarmassignment"
            minSdk = 26
            targetSdk = 35
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
        buildFeatures {
            viewBinding = true
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
        kotlinOptions {
            jvmTarget = "1.8"
        }


    }

    dependencies {

        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        implementation(libs.androidx.activity)
        implementation(libs.androidx.constraintlayout)
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
        implementation("com.google.code.gson:gson:2.8.8") // or the latest version
        implementation("androidx.work:work-runtime-ktx:2.8.1")
        implementation(libs.androidx.viewModel)
        implementation(libs.androidx.liveData)
        implementation(libs.androidx.viewModelSavedState)
        implementation(libs.ssp)
        implementation(libs.sdp)
        implementation(libs.splash)
        implementation(libs.jetbrains.coroutines)
        implementation(libs.kapt.viewModelAnnotationProcessor)
    }
}