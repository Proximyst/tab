import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    implementation(project(":common"))
    compileOnly("com.destroystokyo.paper:paper-api:1.15.2-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.10.6")
}

tasks.withType<ShadowJar> {
    fun relocations(vararg pkgs: String) =
        pkgs.forEach { relocate(it, "com.proximyst.tab.dependencies.$it") }
    mergeServiceFiles()
    minimize()

    relocations(
        "org.jetbrains.annotations",
        "org.intellij.lang.annotations",
        "org.checkerframework",
        "net.kyori.text",
        "net.kyori.minecraft",
        "com.moandjiezana.toml",
        "kotlin"
    )
}