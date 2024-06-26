plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services") //self - register project and download json file, and add in app folder in project mode
    id("androidx.navigation.safeargs.kotlin") //self
    id("com.google.devtools.ksp") //kotlin-ksp instead kotlin-kapt
}

android {
    namespace = "com.androidrider.demokart"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.androidrider.demokart"
        minSdk = 24
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    //self
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    // Firebase Cloud Messaging
    implementation("com.google.firebase:firebase-messaging-ktx:24.0.0")

//self
    // Smooth Bottom Bar
    implementation("com.github.ibrahimsn98:SmoothBottomBar:1.7.9")


    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // Image slider
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")

    // Firebase
    implementation("com.google.firebase:firebase-auth-ktx")// this work for login authentication
    implementation("com.google.firebase:firebase-auth-ktx:23.0.0")
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    implementation("com.google.firebase:firebase-storage-ktx:21.0.0")
    implementation("com.google.firebase:firebase-firestore-ktx:25.0.0")
    // Firebase Analytics
    implementation("com.google.firebase:firebase-analytics-ktx:22.0.2")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // Razorpay
    implementation ("com.razorpay:checkout:1.6.33")

    // SpinKit
    implementation ("com.github.ybq:Android-SpinKit:1.4.0")

    // Circular Image
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    //Gson
    implementation ("com.google.code.gson:gson:2.10.1")
}