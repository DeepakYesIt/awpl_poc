plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
//    id ("com.google.devtools.ksp")
    id ("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
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
        debug {
            val BASE_URL = project.property("BASE_URL")
            buildConfigField("String", "BASE_URL", "${BASE_URL}")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
            val BASE_URL = project.property("BASE_URL")
            buildConfigField("String", "BASE_URL", "${BASE_URL}")
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
        buildConfig = true
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
    implementation(libs.play.services.cast.framework)

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

    // hilt

    implementation("com.google.dagger:hilt-android:2.52")
    kapt("com.google.dagger:hilt-android-compiler:2.52")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("com.hbb20:ccp:2.6.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:5.0.0-alpha.10")
    implementation ("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.10")
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    implementation ("com.airbnb.android:lottie:3.4.0")

    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("io.agora.rtc:full-sdk:4.2.1")
    implementation("in.payu:payu-checkout-pro:2.8.0")
    //firebase
    implementation("com.google.firebase:firebase-messaging:23.2.1")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.google.android.gms:play-services-auth:21.1.1")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("com.github.dhaval2404:imagepicker:2.1")

    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation (platform("com.google.firebase:firebase-bom:33.14.0"))
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
}