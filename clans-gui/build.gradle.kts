plugins {
    `java-library`
    id("net.minecrell.plugin-yml.bukkit") version "0.5.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "org.gepron1x"
version = "0.1"

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://mvn-repo.arim.space/lesser-gpl3/") }
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
}

dependencies {
    compileOnly("net.pl3x.purpur:purpur-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly(project(":clans-plugin", "shadow"))
    compileOnly("cloud.commandframework:cloud-paper:1.8.0")
    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.8")
}

tasks {
    shadowJar {
        relocate("cloud.commandframework", "org.gepron1x.clans.libraries.cloud.commandframework")
        relocate("com.github.stefvanschie.inventoryframework", "org.gepron1x.clans.gui.libraries.if")
    }
}

bukkit {
    name = "DecaliumClansGUI"
    description = "A gui addon for Decalium Clans"
    depend = listOf("DecaliumClans")
    author = "gepron1x"
    apiVersion = "1.16"
    main = "org.gepron1x.clans.gui.DecaliumClansGui"
}