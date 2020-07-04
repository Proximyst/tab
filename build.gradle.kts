import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import nl.javadude.gradle.plugins.license.LicensePlugin
import org.apache.tools.ant.filters.ReplaceTokens
import java.util.Calendar

plugins {
    java
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "6.0.0"
    id("com.github.hierynomus.license") version "0.15.0"
}

allprojects {
    group = "com.proximyst.tab"
    version = "0.1.0"
}

repositories {
    jcenter() // Gradle plugins
}

subprojects {
    apply {
        plugin(JavaLibraryPlugin::class.java)
        plugin(ShadowPlugin::class.java)
        plugin(MavenPublishPlugin::class.java)
        plugin(LicensePlugin::class.java)
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
                includeGroup("net.kyori")
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

        jcenter()
        mavenCentral()
        // }}}
        // </editor-fold>
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

    dependencies {
        compileOnly("org.jetbrains:annotations:19.0.0")
    }
}

allprojects {
    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = sourceCompatibility
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

    java {
        withSourcesJar()
        withJavadocJar()
    }

    tasks.withType<Jar> {
        manifest {
            attributes("Implementation-Version" to project.version.toString())
        }
    }

    if (System.getenv("NEXUS_USERNAME") != null)
        publishing {
            publications {
                create<MavenPublication>("maven") {
                    from(components["java"])
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
