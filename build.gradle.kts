// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlin_version by extra("1.9.0")
    repositories {
        mavenCentral()
        google() // 建議添加 google() repository 以獲取 Android Gradle 插件和其他依賴
    }
    dependencies {
        classpath("com.google.gms:google-services:4.3.14")
        classpath("com.android.tools.build:gradle:8.1.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    }
}

// allprojects {
//     repositories {
//         maven { url = uri("https://www.jitpack.io") }
//     }
// }

plugins {
    id("com.android.application") version "8.1.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.gms.google-services") version "4.3.14" apply false
}
