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
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven("https://hub.jeff-media.com/nexus/repository/jeff-media-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
    implementation("com.jeff_media:CustomBlockData:2.2.0")
    compileOnly(project(":clans-plugin", "shadow"))
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude("org.bukkit", "bukkit")
    }
    compileOnly("me.gepron1x:DecaliumCustomItems:1.1.1")
    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.10")
}

tasks {
    shadowJar {
        relocate("com.github.stefvanschie.inventoryframework", "org.gepron1x.clans.gui.libraries.if")
        relocate("com.jeff_media.customblockdata", "org.gepron1x.clans.gui.libraries.customblockdata")
    }
}

bukkit {
    name = "DecaliumClansGUI"
    description = "A gui addon for Decalium Clans"
    depend = listOf("DecaliumClans", "DecaliumCustomItems")
    author = "gepron1x"
    apiVersion = "1.16"
    main = "org.gepron1x.clans.gui.DecaliumClansGui"
}