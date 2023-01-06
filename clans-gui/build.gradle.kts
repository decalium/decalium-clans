plugins {
    `java-library`
    id("net.minecrell.plugin-yml.bukkit") version "0.5.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "org.gepron1x"
version = "0.1"

repositories {
    mavenCentral()
    maven { url = uri("https://mvn-repo.arim.space/lesser-gpl3/") }
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly(project(":clans-plugin", "shadow"))
    compileOnly("cloud.commandframework:cloud-paper:1.8.0")
    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.8")
}

tasks {
    shadowJar {
        relocate("com.github.stefvanschie.inventoryframework", "org.gepron1x.clans.gui.libraries.if")
    }
}

bukkit {
    name = "DecaliumClansGUI"
    description = "A gui addon for Decalium Clans"
    depend = listOf("DecaliumClans")
    author = "gepron1x"
    apiVersion = "1.18"
    main = "org.gepron1x.clans.gui.DecaliumClansGui"
}