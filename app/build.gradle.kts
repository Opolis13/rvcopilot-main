plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.rvcopilot"
    compileSdk = 35

    defaultConfig {
        applicationId =  "com.example.rvcopilot"
        minSdk = 25
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        buildConfigField(
            "String",
            "RIDB_API_KEY",
            "\"${project.properties["RIDB_API_KEY"]}\""
        )
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,LICENSE.md,LICENSE-notice.md}"
        }
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
    buildFeatures {
        compose = true
        buildConfig = true
    }

}

dependencies {

    implementation ("androidx.compose.foundation:foundation:<latest-version>")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.10.0")

    // firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:32.7.3"))

    // firebase Libs
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")


    // lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    // compose dependencies
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.navigation:navigation-compose:2.7.2")

    // needed this for TopAppBar in the title bar section
    implementation("androidx.compose.material:material")
    //
    implementation("androidx.compose.material:material-icons-extended:1.7.6")
    //implementation(libs.androidx.ui.android)

    // --- Local Unit testing (JVM)
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("app.cash.turbine:turbine:1.0.0")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")

    // --- Android Instrumented Tests -----------
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")


    // --- Compose Debugging/Testing ---
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.4")

    // mockito
    testImplementation("org.mockito:mockito-core:5.2.0")
    androidTestImplementation("org.mockito:mockito-android:5.2.0")
    androidTestImplementation("io.mockk:mockk-android:1.13.7")


    //implementation("androidx.datastore:datastore-preferences:1.0.0")

    //mapping
    implementation("com.google.maps.android:maps-compose:4.4.1")
    implementation("com.google.maps.android:maps-compose-utils:4.4.1")
    implementation("com.google.maps.android:maps-compose-widgets:4.4.1")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.accompanist:accompanist-permissions:0.37.2")


    // room database
    //val room_version = "2.6.1"

    //implementation ("androidx.room:room-runtime:$room_version")
    //implementation("androidx.room:room-ktx:$room_version")
    //ksp("androidx.room:room-compiler:$room_version")

    // pictures
    implementation("io.coil-kt:coil-compose:2.4.0")

    //Dark mode toolbar
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")

    //database for recreation.gov
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.compose.runtime:runtime-livedata")

}