import java.util.Properties
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.github.dcendents.android-maven")
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


android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "0.1.5"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
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
    buildFeatures {
        compose = true
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
    composeOptions {
        composeOptions.kotlinCompilerVersion = "1.3.70-dev-withExperimentalGoogleExtensions-20200424"
        kotlinCompilerExtensionVersion = versionsProperties["version.androidx.ui"].toString()
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:_")
    implementation("androidx.core:core-ktx:_")
    implementation("androidx.ui:ui-framework:_")
    implementation("androidx.ui:ui-foundation:_")
    implementation("androidx.ui:ui-tooling:_")
    implementation("io.coil-kt:coil:_")
}