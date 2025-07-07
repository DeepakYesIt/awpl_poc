// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    // id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false

    // Hilt

    id("com.google.dagger.hilt.android") version "2.52" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://sdk.withpersona.com/android/releases")
        maven("https://storage.zego.im/maven")
        maven("https://maven.payu.in/release")
    }
    dependencies {
        // Updated Gradle plugin
        classpath("com.android.tools.build:gradle:8.4.2")
        // Hilt plugin
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.52")
        classpath("com.google.gms:google-services:4.3.15")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
    }

}


