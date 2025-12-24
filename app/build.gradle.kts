plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.jetpack.compose"
    compileSdk {
        version = release(36)
    }
    defaultConfig {
        applicationId = "com.jetpack.compose"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    with(libs) {
        implementation(androidx.core.ktx)
        implementation(androidx.appcompat)

        implementation(bundles.lifecycle)

        implementation(hilt.android)
        implementation(hilt.navigation)
        ksp(hilt.compiler)

        implementation(platform(libs.androidx.compose.bom))
        implementation(bundles.compose.core)
        implementation(bundles.core.ui)
        implementation(bundles.compose.tooling)
        implementation(bundles.compose.navigation)
        implementation(bundles.androidx.navigation.dependencies)

        implementation(bundles.network)

        implementation(libs.kotlinx.serialization.core)

        debugImplementation(leakcanary)
        implementation(timber)

        testImplementation(test.junit)
        androidTestImplementation(test.extjunit)
        androidTestImplementation(test.espresso)
    }
}
