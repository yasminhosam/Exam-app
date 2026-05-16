import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.secrets.gradle.plugin)

}
//val apiKey = project.findProperty("GEMINI_API_KEY") as? String ?: ""
android {
    namespace = "com.example.onlineexaminationsystem"
    compileSdk = 35

    buildFeatures{
        buildConfig=true
    }
    defaultConfig {
        applicationId = "com.example.onlineexaminationsystem"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

//        buildConfigField("String", "GEMINI_API_KEY", "\"$apiKey\"")
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
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.protolite.well.known.types)
    implementation(libs.androidx.espresso.core)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.hilt.work)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation (libs.kotlinx.coroutines.test)

    //viewmodel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // navigation

    implementation("androidx.navigation:navigation-compose:2.9.5")

    val room_version = "2.8.4"

    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("com.google.code.gson:gson:2.10.1")

    //dagger-hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation ("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation(libs.androidx.material.icons.extended)

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.0")
    implementation (platform("com.google.firebase:firebase-bom:34.9.0"))
    ksp("androidx.hilt:hilt-compiler:1.2.0")


    implementation("com.google.ai.client.generativeai:generativeai:0.6.0")
    implementation(platform("com.google.firebase:firebase-bom:34.12.0"))

    implementation("androidx.datastore:datastore-preferences:1.1.1")
}

secrets {
    // To add your properties file, specify it here. Default is local.properties.
    propertiesFileName = "local.properties"
}