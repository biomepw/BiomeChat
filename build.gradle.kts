plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

version = "3.2.0"

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
    maven {
        name = "Aikar"
        url = uri("https://repo.aikar.co/content/groups/aikar/")
    }
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.16.3-R0.1-SNAPSHOT")
    implementation("co.aikar:acf-paper:0.5.0-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.16")
    annotationProcessor("org.projectlombok:lombok:1.18.16")
}

tasks {
    withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
        archiveFileName.set("BiomeChat-" + project.version + ".jar")
        relocate("co.aikar.commands", "pw.biome.biomechat.acf")
        relocate("co.aikar.locales", "pw.biome.biomechat.locales")
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