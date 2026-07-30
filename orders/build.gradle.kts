import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    `maven-publish`
}

group = "com.danilobarreto.stockapp"

val localProperties = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

fun prop(envName: String): String? =
    System.getenv(envName) ?: localProperties.getProperty(envName)

version = prop("RELEASE_VERSION") ?: "0.1.0-local"

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/dgbarreto/stockapp-orders")
            credentials {
                username = prop("GITHUB_ACTOR")
                password = prop("GITHUB_TOKEN")
            }
        }
    }
}

kotlin {
    jvm("desktop")

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Orders"
            isStatic = true
            freeCompilerArgs += listOf("-Xbinary=bundleId=com.danilobarreto.stockapp.orders")
        }
    }

    androidLibrary {
       namespace = "com.danilobarreto.stockapp.orders"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()

       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
        }
        desktopMain.dependencies {
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.serialization.kotlinxJson)
            implementation(libs.kotlinx.serialization.json)

            implementation("com.danilobarreto.stockapp:designsystem:0.1.2")
        }
        iosMain.dependencies {
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

// CMP 1.11.1 pre-built binaries use UIViewLayoutRegion (UIUtilities.framework), an iOS 26 API
// not present in the Xcode 16.4 / iOS 18.5 SDK. Disable iOS Simulator test linking until
// Xcode 17 (iOS 26 SDK) is installed or CMP is downgraded to a version built with iOS 18 SDK.
tasks.matching {
    it.name == "linkDebugTestIosSimulatorArm64" || it.name == "iosSimulatorArm64Test"
}.configureEach {
    enabled = false
}
