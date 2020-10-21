plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

version = "3.1.1"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
    maven {
        name = "Paper"
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        name = "Jitpack"
        url = uri("https://jitpack.io/")
    }
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.16.3-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("org.projectlombok:lombok:1.18.16")
    annotationProcessor("org.projectlombok:lombok:1.18.16")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.16:0.5.0")
    testImplementation("junit:junit:4.13")
}

tasks {
    withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
        archiveFileName.set("BiomeChat-" + project.version + ".jar")
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(project.properties)
        }
    }

    build {
        dependsOn(shadowJar)
    }

    test {
        useJUnit()
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "pw.biome"
            artifactId = "BiomeChat"
            version = project.property("version").toString()
            from(components["java"])
        }
    }

    repositories {
        maven {
            url = uri("https://maven.husk.pro/repository/maven-public/")

            credentials {
                username = "slave"
                password = if (project.hasProperty("repoPass")) {
                    project.property("repoPass").toString()
                } else {
                    ""
                }
            }
        }
    }
}