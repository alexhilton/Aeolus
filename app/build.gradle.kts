import java.util.*

plugins {
    id("com.android.application")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

kotlin {
    androidTarget {
        @OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    sourceSets {
        androidMain.dependencies {
            // Jetpack
            implementation("androidx.core:core-ktx:1.15.0")
            implementation("androidx.appcompat:appcompat:1.7.0")
            implementation("androidx.activity:activity-compose:1.9.3")
            val lifecycleVersion = "2.8.7"
            implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
            implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
            implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
            val navVersion = "2.8.4"
            implementation("androidx.navigation:navigation-runtime-ktx:$navVersion")
            implementation("androidx.navigation:navigation-compose:$navVersion")
            implementation("androidx.datastore:datastore-preferences:1.1.1")

            // Google Play Services
            implementation("com.google.android.gms:play-services-location:21.3.0")

            // Compose
            implementation(compose.preview)
            implementation(project.dependencies.platform("androidx.compose:compose-bom:2024.02.00"))
            implementation("androidx.compose.ui:ui")
            implementation("androidx.compose.ui:ui-graphics")
            implementation("androidx.compose.ui:ui-tooling-preview")
            implementation("androidx.compose.material3:material3")
            implementation("androidx.compose.material:material")

            // Network
            implementation("com.google.code.gson:gson:2.10.1")
            implementation("com.squareup.retrofit2:retrofit:2.9.0")
            implementation("com.squareup.retrofit2:converter-gson:2.9.0")
            implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

            // Accompanist
            implementation("com.google.accompanist:accompanist-permissions:0.32.0")
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            val lifecycleVersion = "2.8.3"
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel:$lifecycleVersion")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
        }
    }
}

android {
    namespace = "net.toughcoder.aeolus"
    compileSdk = 35

    defaultConfig {
        applicationId = "net.toughcoder.aeolus"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "QWEATHER_API_KEY", "\"${getQWeatherAPIKey()}\"")
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
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Modules in our project
    implementation(project(":qweather"))

    // Room database
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Flavor
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

fun getQWeatherAPIKey(): String? {
    val properties = Properties().apply {
        load(rootProject.file("local.properties").reader())
    }
    return properties["QWEATHER_KEY"] as? String
}