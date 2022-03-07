plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("io.gitlab.arturbosch.detekt").version("1.18.0-RC2")
    kotlin("plugin.serialization")
}

version = "1.0"
val coroutinesVersion = "1.5.0-native-mt"
val serializationVersion = "1.2.2"
val ktorVersion = "1.6.1"
val kotlinStdlibVersion = "1.5.21"
val logbackVersion = "1.2.6"

kotlin {
    android()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinStdlibVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("ch.qos.logback:logback-classic:$logbackVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-android:$ktorVersion")
                implementation("com.auth0.android:auth0:2.+")
                implementation("com.auth0.android:jwtdecode:2.0.0")
            }
        }
    }
}

android {
    compileSdkVersion(30)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
    }

    detekt {
        toolVersion = "1.18.0-RC2"
        config = files("$rootDir/config/detekt/detekt.yml")
        buildUponDefaultConfig = true
        input = files("src/androidMain/kotlin", "src/commonMain/kotlin")
    }
}