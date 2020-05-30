dependencies {
    compileOnly("com.google.code.gson:gson:2.8.0")
    api("com.moandjiezana.toml:toml4j:0.7.4")

    val kyoriTextVersion = "3.0.3"
    api("net.kyori:text-api:$kyoriTextVersion")
    api("net.kyori:text-serializer-gson:$kyoriTextVersion")

    api(kotlin("stdlib-jdk8"))
    api(kotlin("reflect"))
}