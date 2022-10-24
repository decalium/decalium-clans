plugins {
    java
    id("de.nilsdruyen.gradle-ftp-upload-plugin") version "0.1.0"

}

group = "org.gepron1x"
version = "0.1"

repositories {
    mavenCentral()
}

tasks.register("buildDocs") {
    doLast {
        exec {
            workingDir("${project.rootDir}/documentation")
            executable("mkdocs")
            args("build")
        }
    }
}

tasks.register("deployDocs") {
    dependsOn("buildDocs")
    dependsOn("uploadFilesToFtp")
}

configure<de.nilsdruyen.gradle.ftp.UploadExtension> {
    host = property("dclm_ip").toString()
    port = 22
    username = property("dclm_user").toString()
    password = property("dclm_password").toString()
    sourceDir = "${project.rootDir}/documentation/site"
    targetDir = "/home/${property("dclm_user").toString()}/docs/"
}

dependencies {}