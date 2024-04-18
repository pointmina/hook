@file:Suppress("UNUSED_EXPRESSION")

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

android {
    namespace = "com.hanto.Hook"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hanto.Hook"
        minSdk = 24
        targetSdk = 34
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

    buildFeatures{
        viewBinding = true
        buildConfig = true
        dataBinding = true
    }


}

dependencies {
    //카카오 로그인
    implementation ("com.kakao.sdk:v2-all:2.20.1") // 전체 모듈 설치, 2.11.0 버전부터 지원 헷갈려서 다 넣엇음 걍

    //recyclerView SwipeRefreshLayout
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    //flexBox
    implementation ("com.google.android.flexbox:flexbox:3.0.0")
    implementation ("com.google.android.material:material:1.11.0")
    //구글 로긴
    implementation(platform("com.google.firebase:firebase-bom:31.5.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation ("com.google.android.gms:play-services-auth:20.5.0")
//    implementation platform('com.google.firebase:firebase-bom:31.5.0')
//    implementation 'com.google.firebase:firebase-auth-ktx'
//    implementation 'com.google.android.gms:play-services-auth:20.5.0'
    implementation("androidx.annotation:annotation:1.6.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.activity:activity:1.8.0")
    val nav_version = "2.7.7"
    // Kotlin
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}