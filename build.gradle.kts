// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.8.2" apply false
    id("com.android.library") version "8.8.2" apply false
    id("org.jetbrains.kotlin.android") version "2.1.10" apply false
    id("com.google.devtools.ksp") version "2.1.10-1.0.29" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.10" apply false
}

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.10")
        classpath("androidx.compose.compiler:compiler:1.5.3")

    }
}

// Define extra properties properly in Kotlin
val compose_ui_version by extra("1.7.7")