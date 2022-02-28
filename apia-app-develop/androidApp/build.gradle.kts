plugins {
    id("com.android.application")
    id("io.gitlab.arturbosch.detekt").version("1.18.0-RC2")
    kotlin("android")
    id("kotlin-android")
}

dependencies {
    implementation(project(":shared"))
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.github.skydoves:powerspinner:1.1.9")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.github.skydoves:progressview:1.1.2")
    implementation("com.karumi:dexter:6.2.3")
}

android {
    compileSdkVersion(30)
    defaultConfig {
        applicationId = "edu.codespring.ro.apiaapp.android"
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
        manifestPlaceholders["auth0Domain"] = "@string/com_auth0_domain"
        manifestPlaceholders["auth0Scheme"] = "demo"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        viewBinding = true
    }

    detekt {
        toolVersion = "1.18.0-RC2"
        config = files("$rootDir/config/detekt/detekt.yml")
        buildUponDefaultConfig = true
    }

}