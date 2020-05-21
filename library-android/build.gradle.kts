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
import org.jlleitschuh.gradle.ktlint.reporter.*

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    // "io.hkhc.simplepublisher" must be after "com.android.library"
    // so that libraryVariants is configured before simplePublisher
    id("io.hkhc.simplepublisher")
    id("digital.wup.android-maven-publish") version "3.6.2"
    id("org.jlleitschuh.gradle.ktlint")
    id("io.gitlab.arturbosch.detekt")
    // for build script debugging
    id("com.dorongold.task-tree")
}

/*
 It is needed to make sure every version of java compiler to generate same kind of bytecode.
 Without it and build this with java 8+ compiler, then the project build with java 8
 will get error like this:
   > Unable to find a matching variant of <your-artifact>:
      - Variant 'apiElements' capability <your-artifact>:
          - Incompatible attributes:
              - Required org.gradle.jvm.version '8' and found incompatible value '13'.
              - Required org.gradle.usage 'java-runtime' and found incompatible value 'java-api'.
              ...
 */
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    dokka {
        outputFormat = "html"
        outputDirectory = "$buildDir/dokka"
        // set true to omit intra-project dependencies
        disableAutoconfiguration = true
    }
}

android {
    compileSdkVersion(28)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    testOptions {
        with(unitTests) {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

detekt {
    buildUponDefaultConfig = true
    config = files("$rootDir/detekt/detekt-config.yml")
}

ktlint {
    debug.set(false)
    verbose.set(false)
    coloredOutput.set(true)
    reporters {
        reporter(ReporterType.PLAIN)
        reporter(ReporterType.CHECKSTYLE)
    }
}

android.libraryVariants.configureEach {
    val variantName = name

    if (variantName == "release") {
        simplyPublish {
            useGpg = true
            variant = variantName
            pubComponent = "android"
            sourcesPath = files(javaCompileProvider.get().source)
        }
    }
}

@Suppress("GradleDependency")
dependencies {

    implementation(kotlin("stdlib-jdk8", "1.3.71"))

    implementation("io.hkhc.log:ihlog-android:_")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:_")
    implementation("androidx.appcompat:appcompat:_")
    implementation("androidx.core:core-ktx:_")
    implementation("androidx.recyclerview:recyclerview:_")
    implementation("androidx.paging:paging-runtime:_")
    implementation("androidx.paging:paging-runtime-ktx:_")
    testImplementation("junit:junit:_")
    testImplementation("androidx.test:core:_")
    testImplementation("androidx.arch.core:core-testing:_")
    testImplementation("io.mockk:mockk:_")
    testImplementation("org.assertj:assertj-core:_")
    androidTestImplementation("androidx.test.ext:junit:_")
    androidTestImplementation("androidx.test.espresso:espresso-core:_")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:_")

    testImplementation("org.robolectric:robolectric:_")
}
