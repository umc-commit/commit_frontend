plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.example.commit"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.commit"
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
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compiler.get()
    }
}

dependencies {
    // 기본 AndroidX 및 Material
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Compose Core
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation("androidx.compose.material:material:1.6.0")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation ("androidx.navigation:navigation-compose:2.7.5")
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.activity.compose)

    // Compose 기능 추가
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.navigation.compose)
    implementation(libs.compose.runtime.livedata)
    implementation(libs.androidx.browser)
    implementation(libs.cronet.embedded)
    debugImplementation(libs.compose.ui.tooling)
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.7")


    // 테스트
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:34.0.0"))

    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    
    // Gson for JSON serialization
    implementation("com.google.code.gson:gson:2.10.1")

    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    // Gson
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    // OKhttp logging intercepter
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")

    implementation("com.google.android.flexbox:flexbox:3.0.0")
}
