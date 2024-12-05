import java.util.*

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
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
            implementation(libs.androidx.ktx)
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.activity)
            implementation(libs.androidx.lifecycleViewmodelCompose)
            implementation(libs.androidx.lifecycleRuntime)
            implementation(libs.androidx.navigationRuntime)
            implementation(libs.androidx.navigation)
            implementation(libs.androidx.datastore)

            // Google Play Services
            implementation(libs.google.location)

            // Compose
            implementation(compose.preview)
            implementation(project.dependencies.platform("androidx.compose:compose-bom:2024.02.00"))
            implementation("androidx.compose.ui:ui")
            implementation("androidx.compose.ui:ui-graphics")
            implementation("androidx.compose.ui:ui-tooling-preview")
            implementation("androidx.compose.material3:material3")
            implementation("androidx.compose.material:material")

            // Network
            implementation(libs.google.gson)
            implementation(libs.retrofit)
            implementation(libs.retrofit.gson)
            implementation(libs.okhttp.logging)

            // Accompanist
            implementation(libs.accompanist.permission)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle)
            implementation(libs.androidx.lifecycleViewmodel)
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
    implementation(libs.androidx.room)
    implementation(libs.androidx.roomRuntime)
    annotationProcessor(libs.androidx.roomCompiler)
    ksp(libs.androidx.roomCompiler)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test)
    androidTestImplementation(libs.androidx.espresso)
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