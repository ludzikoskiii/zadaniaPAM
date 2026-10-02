plugins {
    id("com.android.application")
}

android {
    namespace = "pl.pam.czujniki"
    compileSdk = 34

    defaultConfig {
        applicationId = "pl.pam.czujniki"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.22"))
    implementation("androidx.appcompat:appcompat:1.7.0")
}
