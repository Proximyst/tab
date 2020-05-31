dependencies {
    compileOnly("com.google.code.gson:gson:2.8.0")
    api("com.moandjiezana.toml:toml4j:0.7.4") {
        exclude("com.google.code.gson")
    }

    val kyoriTextVersion = "3.0.3"
    api("net.kyori:text-api:$kyoriTextVersion")
    api("net.kyori:text-serializer-gson:$kyoriTextVersion") {
        exclude("com.google.code.gson")
    }
    api("net.kyori:text-serializer-legacy:$kyoriTextVersion")

    api(kotlin("stdlib-jdk8"))
    api(kotlin("reflect"))
}