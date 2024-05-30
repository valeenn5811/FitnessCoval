plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.fitnesscoval"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.fitnesscoval"
        minSdk = 23
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.activity)
    implementation(libs.volley)
    implementation(files("src/main/java/com/example/fitnesscoval/ui/libs/activation-1.1.jar"))
    implementation(files("src/main/java/com/example/fitnesscoval/ui/libs/javax.mail.jar"))
    implementation(fileTree(mapOf(
            "dir" to "C:\\Users\\gonva\\AppData\\Local\\Android\\Sdk\\platforms\\android-23",
            "include" to listOf("*.aar", "*.jar")
    )))
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}