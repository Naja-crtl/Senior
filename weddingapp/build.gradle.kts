plugins {
    alias(libs.plugins.android.application)

}

android {
    namespace = "com.example.weddingapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.weddingapp"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Firebase platform with Firebase Authentication, Firestore, and Analytics
    implementation(platform("com.google.firebase:firebase-bom:32.3.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.firebaseui:firebase-ui-firestore:8.0.1")

    // Google Mobile Ads SDK
    implementation("com.google.android.gms:play-services-ads:23.6.0")

    // Play Services Authentication (optional, needed for some APIs)
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Implementing AI chatbot DialogFlow from google
//    implementation ("com.google.cloud:google-cloud-dialogflow:v2")
//    implementation ("com.google.code.gson:gson:2.8.8")
//    implementation ("org.apache.commons:commons-lang3:3.12.0")


}

// Apply Google Services plugin
apply (plugin = "com.google.gms.google-services")
