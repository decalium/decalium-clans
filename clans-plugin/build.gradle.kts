plugins {
    java
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.3.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "org.gepron1x"
version = "0.1"

repositories {
    mavenCentral()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }

}

dependencies {
    implementation(project(":clans-api"))
   // compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    paperDevBundle("1.18-R0.1-SNAPSHOT")
    implementation("org.jdbi:jdbi3-core:3.24.1")
    implementation("cloud.commandframework:cloud-paper:1.5.0")
    implementation("space.arim.dazzleconf:dazzleconf-ext-snakeyaml:1.2.1") {
        exclude(group = "org.yaml", module = "snakeyaml")
    }
    compileOnly("me.clip:placeholderapi:2.10.0")
    implementation("com.zaxxer:HikariCP:5.0.0")
    implementation ("net.kyori:adventure-text-minimessage:4.2.0-SNAPSHOT") {
        exclude(group = "net.kyori", module = "adventure-api")
    }
}

var libraryPackage = "org.gepron1x.clans.libraries"

tasks {
    shadowJar {
        relocate("space.arim.dazzleconf", "$libraryPackage.dazzleconf")
        relocate("org.jdbi", "$libraryPackage.jdbi")
        relocate("cloud.commandframework", "$libraryPackage.cloud.commandframework")
        relocate("com.github.benmanes.caffeine", "$libraryPackage.caffeine")
        relocate("io.leangen.geantyref", "$libraryPackage.geantyref")
        relocate("com.zaxxer.hikari", "$libraryPackage.hikari")

    }
}







bukkit {
    main = "org.gepron1x.clans.plugin.DecaliumClansPlugin"
    description = "Shining clans plugin"
    apiVersion = "1.17"
    authors = listOf("gepron1x", "manya")
    depend = listOf("PlaceholderAPI")
}

