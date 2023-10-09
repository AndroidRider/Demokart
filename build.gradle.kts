// Top-level build file where you can add configuration options common to all sub-projects/modules.

// self - Admin, Client (both)
buildscript {
    repositories {
        google()
        maven ( url = "https://www.jitpack.io") // self- Client
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
        val nav_version = "2.7.4"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version") //Admin, Client (both)
    }
}

plugins {
    id("com.android.application") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false

    id("com.google.gms.google-services") version "4.3.15" apply false //Admin, Client (both)


    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false // self- Client
}