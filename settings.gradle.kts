rootProject.name = "StockAppOrders"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

val localProperties = java.util.Properties().apply {
    val f = file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
val useLocalDesignSystem = localProperties.getProperty("useLocalDesignSystem", "false").toBoolean()
val useLocalAuth = localProperties.getProperty("useLocalAuth", "false").toBoolean()

fun prop(name: String): String? =
    System.getenv(name) ?: localProperties.getProperty(name)

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        mavenLocal()
        maven {
            name = "GitHubPackagesDesignSystem"
            url = uri("https://maven.pkg.github.com/dgbarreto/stockapp-designsystem")
            credentials {
                username = prop("GITHUB_ACTOR")
                password = prop("GITHUB_TOKEN")
            }
        }
        maven {
            name = "GitHubPackagesAuth"
            url = uri("https://maven.pkg.github.com/dgbarreto/stockapp-auth")
            credentials {
                username = prop("GITHUB_ACTOR")
                password = prop("GITHUB_TOKEN")
            }
        }
    }
}

include(":orders")
include(":sample")
include(":sample-android")

if(useLocalDesignSystem){
    includeBuild("../stockapp-designsystem"){
        dependencySubstitution {
            substitute(module("com.danilobarreto.stockapp:designsystem"))
                .using(project(":designsystem"))
        }
    }
}
if (useLocalAuth) {
    includeBuild("../stockapp-auth") {
        dependencySubstitution {
            substitute(module("com.danilobarreto.stockapp:auth"))
                .using(project(":auth"))
        }
    }
}
