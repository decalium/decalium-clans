import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    java
    `java-library`
    // id("io.papermc.paperweight.userdev") version "1.3.5"
    id("xyz.jpenilla.run-paper") version "1.0.6"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val shadowJar: com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar by tasks
shadowJar.apply {
    mergeServiceFiles()
}

group = "org.gepron1x"
version = "0.1"

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://mvn-repo.arim.space/lesser-gpl3/") }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = uri("https://jitpack.io") }
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.decalium.ru/shapshots")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")

    maven("https://nexus.sirblobman.xyz/public/")

}

dependencies {
    api(project(":clans-api"))
    implementation("org.bstats:bstats-bukkit:3.0.0")
    implementation("com.github.Xezard.XGlow:XGlow:1.1.0")
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
    // paperDevBundle("1.18.2-R0.1-SNAPSHOT")
    compileOnly("de.hexaoxi:carbonchat-api:3.0.0-SNAPSHOT") {
        exclude("net.kyori", "adventure-text-minimessage")
        exclude("ninja.egg82", "messenger-api")
    }
    implementation("org.jdbi:jdbi3-core:3.37.1") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation("org.flywaydb:flyway-core:9.16.0") {
        exclude("com.fasterxml.jackson.dataformat", "jackson-dataformat-toml")
    }
    implementation("org.flywaydb:flyway-mysql:9.16.0") {
        exclude("com.fasterxml.jackson.dataformat", "jackson-dataformat-toml")
    }
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.5") {
        exclude("org.checkerframework", "checker-qual")
        exclude("com.google.errorprone", "error_prone_annotations")
    }
    implementation("cloud.commandframework:cloud-paper:1.8.3") {
        exclude("org.checkerframework", "checker-qual")
    }
    api("cloud.commandframework:cloud-minecraft-extras:1.8.3") {
        exclude("net.kyori", "adventure-api")
        exclude("net.kyori", "adventure-text-serializer-plain")
    }
    implementation("space.arim.dazzleconf:dazzleconf-ext-snakeyaml:1.2.1") {
        exclude(group = "org.yaml", module = "snakeyaml")
    }
    implementation("com.h2database:h2:2.1.214")
    compileOnly("me.clip:placeholderapi:2.10.9")
    implementation("com.zaxxer:HikariCP:5.0.1") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.0") {
        exclude("org.bukkit", "bukkit")
    }
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude("org.bukkit", "bukkit")
    }
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.8.1")
    compileOnly("com.github.sirblobman.api:core:2.9-SNAPSHOT")
    compileOnly("com.github.sirblobman.combatlogx:api:11.4-SNAPSHOT")


    testImplementation("net.kyori:adventure-text-minimessage:4.13.1")
    testImplementation("com.google.guava:guava:32.0.0-jre")
    testImplementation("com.google.code.gson:gson:2.10.1")


}


var libraryPackage = "org.gepron1x.clans.libraries"

fun com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.relocateDependency(pkg: String) {
    relocate(pkg, "$libraryPackage.$pkg")
}

tasks {
    shadowJar {
        relocateDependency("org.antlr")
        relocateDependency("org.flywaydb")
        relocateDependency("org.apiguardian")
        relocateDependency("space.arim")
        relocateDependency("org.jdbi")
        // relocateDependency("org.h2")
       // relocateDependency("com.fasterxml")
        relocateDependency("cloud.commandframework")
        relocateDependency("com.github.benmanes.caffeine")
        relocateDependency("io.leangen.geantyref")
        relocateDependency("com.zaxxer.hikari")
        relocateDependency("org.bstats")
        relocateDependency("ru.xezard.glow")

    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
        options.compilerArgs.add("-parameters")
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.19.4")
        jvmArgs("-Xms128M", "-Xmx512M")

    }
}







bukkit {
    name = "DecaliumClans"
    main = "org.gepron1x.clans.plugin.DecaliumClansPlugin"
    description = "Shining clans plugin"
    apiVersion = "1.19"
    authors = listOf("gepron1x", "manya")
    website = "https://clans.decalium.ru"
    softDepend = listOf("PlaceholderAPI", "CarbonChat", "WorldGuard", "Vault", "DecentHolograms", "ProtocolLib", "Hyperverse", "CombatLogX")
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD

}

