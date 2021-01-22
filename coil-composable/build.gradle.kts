import java.util.Properties
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven")
}

group = "com.github.luca992"

fun getProperties(path: String) : Properties {
    val properties = Properties()
    val file: File = rootProject.file(path)
    if (file.exists()) {
        file.inputStream().use { properties.load(it) }
    }
    return properties
}

val versionsProperties : Properties by lazy {
    val versions = getProperties("versions.properties")
    versions
}

repositories{
    google()
    jcenter()
}


android {
    compileSdkVersion(30)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "0.3.4"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    sourceSets {
        val main by getting {}
        main.setRoot("src/androidMain")
        main.java.srcDirs("src/androidMain/kotlin")
        main.manifest.srcFile("src/androidMain/AndroidManifest.xml")
        main.res.srcDir("src/androidMain/res")
        main.assets.srcDir("src/androidMain/assets")
        val test by getting {}
        test.java.srcDir("src/androidTest/kotlin")
        val androidTest by getting {}
        androidTest.java.srcDir("src/androidAndroidTest/kotlin")
    }
    lintOptions {
        disable("GradleDependency")
    }
    packagingOptions {
        // https://github.com/Kotlin/kotlinx.coroutines/issues/2023
        exclude("META-INF/AL2.0")
        exclude("META-INF/LGPL2.1")
    }
    buildFeatures {
        compose = true
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
    composeOptions {
        kotlinCompilerVersion = "1.4.21"
        kotlinCompilerExtensionVersion = versionsProperties["version.androidx.compose.ui"].toString()
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xallow-jvm-ir-dependencies")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:_")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:_")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:_")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:_") {
        // conflicts with mockito due to direct inclusion of byte buddy
        // https://github.com/Kotlin/kotlinx.coroutines/issues/2023
        exclude(group= "org.jetbrains.kotlinx", module= "kotlinx-coroutines-debug")
    }
    implementation("androidx.core:core-ktx:_")
    implementation("androidx.compose.ui:ui:_")
    implementation("androidx.compose.foundation:foundation:_")
    implementation("androidx.compose.ui:ui-tooling:_")
    implementation("androidx.compose.ui:ui-test:_")
    implementation("io.coil-kt:coil:_")
}
