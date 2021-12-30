plugins {
    java
    `java-library`
}

group = "org.gepron1x"
version = "0.1"

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://mvn-repo.arim.space/lesser-gpl3/") }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.18-R0.1-SNAPSHOT")
    api("space.arim.omnibus:omnibus:1.1.0-RC2")
    implementation("org.jetbrains:annotations:22.0.0")
}
