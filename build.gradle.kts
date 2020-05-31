import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import nl.javadude.gradle.plugins.license.LicensePlugin
import org.apache.tools.ant.filters.ReplaceTokens
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "5.2.0"
    kotlin("jvm") version "1.3.72"
    id("org.jetbrains.dokka") version "0.10.1"
    id("com.github.hierynomus.license") version "0.15.0"
}

allprojects {
    group = "com.proximyst.tab"
    version = "0.2.0"
}

subprojects {
    apply {
        plugin<JavaLibraryPlugin>()
        plugin<MavenPublishPlugin>()
        plugin<ShadowPlugin>()
        plugin<DokkaPlugin>()
        plugin<KotlinPlatformJvmPlugin>()
        plugin<LicensePlugin>()
    }

    repositories {
        // <editor-fold desc="Repositories" defaultstate="collapsed">
        // {{{ Repositories
        maven {
            name = "spigotmc"
            url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

            content {
                includeGroup("org.bukkit")
                includeGroup("org.spigotmc")
            }
        }

        maven {
            name = "sonatype"
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")

            content {
                includeGroup("net.md-5")
            }
        }

        maven {
            name = "papermc-snapshots"
            url = uri("https://papermc.io/repo/repository/maven-snapshots/")

            content {
                includeGroup("com.destroystokyo.paper")
                includeGroup("io.github.waterfallmc")
                includeGroup("io.papermc")
            }
        }

        maven {
            name = "papermc"
            url = uri("https://papermc.io/repo/repository/maven-public/")

            content {
                includeGroup("com.destroystokyo.paper")
                includeGroup("io.github.waterfallmc")
                includeGroup("io.papermc")
            }
        }

        maven {
            name = "clip"
            url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")

            content {
                includeGroup("me.clip")
            }
        }

        maven {
            name = "proxi-nexus"
            url = uri("https://nexus.proximyst.com/repository/maven-public/")
        }
        // }}}
        // </editor-fold>

        mavenCentral()
    }

    tasks.withType<ProcessResources> {
        include("*.yml")
        include("*.toml")
        filter<ReplaceTokens>(
            "tokens" to mapOf(
                "VERSION" to project.version.toString()
            )
        )
    }
}

allprojects {
    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = sourceCompatibility
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            javaParameters = true
        }
    }

    repositories {
        jcenter() // Required for Gradle plugins
    }

    tasks.withType<ShadowJar> {
        this.archiveClassifier.set(null as String?)
        this.archiveBaseName.set(
            if (project == rootProject) project.name
            else "${rootProject.name}-${project.name}"
        )
        this.destinationDirectory.set(
            rootProject
                .tasks
                .shadowJar.get()
                .destinationDirectory.get()
        )
    }

    val dokka by tasks.getting(DokkaTask::class) {
        outputDirectory = "$buildDir/dokka"
        outputFormat = "html"
    }

    val dokkaJar by tasks.creating(Jar::class) {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        description = "Assembles Kotlin docs with Dokka"
        archiveClassifier.set("javadoc")
        dependsOn(dokka)
        from("$buildDir/dokka")
    }

    val sourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    tasks.withType<Jar> {
        manifest {
            attributes("Implementation-Version" to project.version.toString())
        }
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                artifact(dokkaJar)
                artifact(sourcesJar.get())
            }
        }
        repositories {
            maven {
                name = "proxi-nexus"
                url = uri("https://nexus.proximyst.com/repository/maven-any/")
                credentials {
                    username = System.getenv("NEXUS_USERNAME") // Provided by Drone
                    password = System.getenv("NEXUS_PASSWORD") // Provided by Drone
                }
            }
        }
    }

    license {
        header = rootProject.file("LICENCE-HEADER")
        ext["year"] = Calendar.getInstance().get(Calendar.YEAR)
        ext["name"] = "Mariell Hoversholm"
        include("**/*.kt")
    }
}
