/*
 * Copyright (c) 2020. Herman Cheung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

plugins {
    id("com.android.application")
    id("kotlin-android")
}


android {
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "io.hkhc.recyclerviewkit.io.hkhc.recyclerviewkit.demo"
        minSdkVersion(21)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

@Suppress("GradleDependency")
dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib-jdk8", "_"))
    implementation(project(":library-android"))
    implementation("androidx.appcompat:appcompat:_")
    implementation("androidx.constraintlayout:constraintlayout:_")
    implementation("androidx.recyclerview:recyclerview:_")
    implementation("androidx.paging:paging-runtime:_")
    implementation("androidx.paging:paging-runtime-ktx:_")
    implementation("io.reactivex.rxjava2:rxjava:_")
    implementation("io.reactivex.rxjava2:rxkotlin:_")
    implementation("io.reactivex.rxjava2:rxandroid:_")
    testImplementation("junit:junit:_")
    androidTestImplementation("com.android.support.test:runner:_")
    androidTestImplementation("com.android.support.test.espresso:espresso-core:_")
}
