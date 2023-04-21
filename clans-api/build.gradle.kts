plugins {
    java
    `java-library`
    id("com.github.johnrengelman.shadow") version "7.1.2"

}

group = "org.gepron1x"
version = "0.1"

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven{ url = uri("https://mvn-repo.arim.space/lesser-gpl3/") }
}

dependencies {
    api("space.arim.omnibus:omnibus:1.1.0-RC2")
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:23.0.0")
}

tasks {



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
