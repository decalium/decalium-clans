plugins {
    java
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.3.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.0"
}

group = "org.gepron1x"
version = "0.1"

repositories {
    mavenCentral()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }

}

dependencies {
    implementation(project(":clans-api"))
   // compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    paperDevBundle("1.18-R0.1-SNAPSHOT")
    implementation("org.jdbi:jdbi3-core:3.24.1")
    implementation("space.arim.dazzleconf:dazzleconf-ext-snakeyaml:1.2.1")
}

var libraryPackage = "org.gepron1x.clans.libraries"





bukkit {
    main = "org.gepron1x.clans.plugin.DecaliumClansPlugin"
    apiVersion = "1.17"
    authors = listOf("gepron1x", "manya")
    depend = listOf("PlaceholderAPI")
}

