@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kapt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
//    alias(libs.plugins.objectbox)
    id("io.objectbox")
}
//buildscript {
//    ext.objectboxVersion = "3.7.1"
//    repositories {
//        mavenCentral()
//        // Note: 2.9.0 and older are available on jcenter()
//    }
//    dependencies {
//        // Android Gradle Plugin 3.4.0 or later supported.
//        classpath("com.android.tools.build:gradle:8.0.0")
//        classpath("io.objectbox:objectbox-gradle-plugin:$objectboxVersion")
//    }
//}

android {
    namespace = "com.xd.mvvm.boilerplate.model"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.okhttp)
    implementation(libs.moshi)
    implementation(libs.moshikotlin)
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.junit4)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)

    // Hilt
    implementation(libs.hilt.android.core)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.hilt.compiler)
}