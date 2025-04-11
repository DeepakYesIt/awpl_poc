plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
//    id ("com.google.devtools.ksp")
}

android {
    namespace = "com.bussiness.awpl"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bussiness.awpl"
        minSdk = 24
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    //sdp and ssp
    implementation(libs.ssp.android)
    implementation(libs.sdp.android)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.ui.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //viewpager
    implementation( libs.androidx.viewpager2)
    implementation (libs.material.v190)
    implementation (libs.dotsindicator)
    //splash
    implementation (libs.androidx.core.splashscreen)
    //calendar
    implementation (libs.calendarview)
    //constraint layout
    implementation (libs.androidx.constraintlayout.v221)
    //glide
    implementation (libs.glide)
    implementation (libs.annotations)
//    ksp (libs.ksp)
    //shapeAble image
    implementation (libs.material.v1120)






}