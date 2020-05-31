import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    implementation(project(":common"))
    implementation("com.google.code.gson:gson:2.8.0")
    compileOnly("io.github.waterfallmc:waterfall-api:1.15-SNAPSHOT")
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
        "com.google.gson",
        "kotlin"
    )
}