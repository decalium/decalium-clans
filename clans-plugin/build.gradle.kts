plugins {
    java
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.3.0"
    id("xyz.jpenilla.run-paper") version "1.0.6"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
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

}

dependencies {
    implementation(project(":clans-api"))
   // compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    paperDevBundle("1.18-R0.1-SNAPSHOT")
    implementation("org.jdbi:jdbi3-core:3.25.0") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation("cloud.commandframework:cloud-paper:1.6.1")
    implementation("space.arim.dazzleconf:dazzleconf-ext-snakeyaml:1.2.1") {
        exclude(group = "org.yaml", module = "snakeyaml")
    }
    implementation("com.h2database:h2:2.0.204")
    compileOnly("me.clip:placeholderapi:2.10.9")
    implementation("com.zaxxer:HikariCP:5.0.0") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation ("net.kyori:adventure-text-minimessage:4.10.0-SNAPSHOT") {
        exclude(group = "net.kyori", module = "adventure-api")
    }
    compileOnly("net.draycia:carbonchat-api:2.0.6") {
        exclude("net.kyori", "adventure-text-minimessage")
    }
}

var libraryPackage = "org.gepron1x.clans.libraries"

tasks {
    shadowJar {

        relocate("org.antlr", "$libraryPackage.antlr")
        relocate("net.kyori.adventure.text.minimessage", "$libraryPackage.minimessage")
        relocate("space.arim.dazzleconf", "$libraryPackage.dazzleconf")
        relocate("space.arim.omnibus", "$libraryPackage.omnibus")
        relocate("org.jdbi", "$libraryPackage.jdbi")
        relocate("org.h2", "$libraryPackage.h2")
        relocate("cloud.commandframework", "$libraryPackage.cloud.commandframework")
        relocate("com.github.benmanes.caffeine", "$libraryPackage.caffeine")
        relocate("io.leangen.geantyref", "$libraryPackage.geantyref")
        relocate("com.zaxxer.hikari", "$libraryPackage.hikari")

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
        minecraftVersion("1.18.1")
        jvmArgs("-Xms128M", "-Xmx512M")
    }
}







bukkit {
    name = "DecaliumClans"
    main = "org.gepron1x.clans.plugin.DecaliumClansPlugin"
    description = "Shining clans plugin"
    apiVersion = "1.17"
    authors = listOf("gepron1x", "manya")
    depend = listOf("PlaceholderAPI")
}

