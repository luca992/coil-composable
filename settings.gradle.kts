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
        classpath ("com.android.tools.build:gradle:4.1.0-alpha09")
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
    }
}

bootstrapRefreshVersionsAndDependencies()


include(":coil-composable")
rootProject.name = "coil-composable"

enableFeaturePreview("GRADLE_METADATA")
