plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "org.gepron1x"
version = "0.1"

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://mvn-repo.arim.space/lesser-gpl3/") }
}

dependencies {
    implementation(project(":clans-plugin", "shadow"))
    implementation("net.kyori:adventure-text-minimessage:4.12.0-SNAPSHOT") {
        exclude("net.kyori", "adventure-api")
    }

}

tasks {
    shadowJar {
        relocate("net.kyori.adventure.text.minimessage", "org.gepron1x.clans.libraries.minimessage")
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
}