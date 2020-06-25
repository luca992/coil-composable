import de.fayard.dependencies.bootstrapRefreshVersionsAndDependencies

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }
}


buildscript {
    val versionsProperties : java.util.Properties by lazy {
        val versions = java.util.Properties()
        val localProperties: File = File(rootProject.projectDir.absoluteFile.absolutePath+"/versions.properties")
        if (localProperties.exists()) {
            localProperties.inputStream().use { versions.load(it) }
        }
        versions
    }
    val kotlinVersion = "${versionsProperties["version.kotlin"]}"


    repositories {
        google()
        jcenter()
        gradlePluginPortal()
    }
    dependencies{
        classpath("de.fayard:dependencies:0.5.8")
        classpath ("com.android.tools.build:gradle:4.2.0-alpha02")
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
    }
}

bootstrapRefreshVersionsAndDependencies()


include(":coil-composable")
rootProject.name = "coilComposable"

enableFeaturePreview("GRADLE_METADATA")
