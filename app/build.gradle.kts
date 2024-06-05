@file:Suppress("UNUSED_EXPRESSION")

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
}

android {
    namespace = "com.hanto.hook"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hanto.hook"
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
                "proguard-rules.pro")
            buildConfigField("String", "BASE_URL", "\"http://ec2-3-39-192-148.ap-northeast-2.compute.amazonaws.com:3030\"")
            buildConfigField("String", "KAKAO_LOGIN_URL", "\"http://ec2-3-39-192-148.ap-northeast-2.compute.amazonaws.com:3030/api/auth/kakao/signin\"")
            buildConfigField("String", "KAKAO_REDIRECT", "\"http://ec2-3-39-192-148.ap-northeast-2.compute.amazonaws.com:3030/api/auth/kakao/get-authorization-code?code=\"")
        }
        debug {
            buildConfigField("String", "BASE_URL", "\"http://ec2-3-39-192-148.ap-northeast-2.compute.amazonaws.com:3030\"")
            buildConfigField("String", "KAKAO_LOGIN_URL", "\"http://ec2-3-39-192-148.ap-northeast-2.compute.amazonaws.com:3030/api/auth/kakao/signin\"")
            buildConfigField("String", "KAKAO_REDIRECT", "\"http://ec2-3-39-192-148.ap-northeast-2.compute.amazonaws.com:3030/api/auth/kakao/get-authorization-code?code=\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        //noinspection DataBindingWithoutKapt
        dataBinding = true
    }


}

dependencies {
    // Navigation 관련
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")


    // Android, Default Layout 관련
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.annotation:annotation:1.7.1")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.fragment:fragment-ktx:1.6.2")


    // 디자인 관련
    // Material Design 관련
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.compose.material3:material3:1.2.1")
    //SwipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    //flexBox
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    // Shimmer
    implementation("com.facebook.shimmer:shimmer:0.5.0")


    // 저장소 관련 (토큰)
    // DataStore
    implementation ("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.datastore:datastore-preferences-core-jvm:1.1.1")
    implementation("androidx.datastore:datastore-core-android:1.1.1")


    // API 관련
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.3.0")
    // Retrofit 부가 Lib
    implementation("com.squareup.okio:okio:3.6.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.6.4") // Scalars 변환기 라이브러리
    // gson
    implementation("com.google.code.gson:gson:2.10.1")
    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    // jsoup
    implementation ("org.jsoup:jsoup:1.13.1")
    // glide (이미지 로드)
    implementation ("com.github.bumptech.glide:glide:4.12.0")


    // 생명주기, MVVM 관련
    // paging
    implementation("androidx.paging:paging-runtime-ktx:3.2.1")
    // coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")


    // Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    //LeakCanary
    debugImplementation ("com.squareup.leakcanary:leakcanary-android:2.9.1")
}