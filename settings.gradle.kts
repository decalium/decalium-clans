
pluginManagement {
    repositories {
        maven("https://papermc.io/repo/repository/maven-public/")
        gradlePluginPortal()
    }
}


rootProject.name = "decalium-clans-rewrite"
include("clans-plugin")
include("clans-api")


