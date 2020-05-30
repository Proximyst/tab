package com.proximyst.tab.common.config

import com.moandjiezana.toml.Toml
import com.moandjiezana.toml.TomlWriter
import com.proximyst.tab.common.ITabPlatform
import java.io.File
import java.time.ZoneOffset
import java.util.*

/**
 * A configuration in the TOML language represented by a [TomlKt].
 */
@Suppress("FINITE_BOUNDS_VIOLATION_IN_JAVA")
class TomlConfiguration<in P : ITabPlatform<*>>(
    private val plugin: P,
    private val file: File,
    private val classPathName: String? = file.name
) {
    lateinit var toml: TomlKt
        private set

    init {
        reload()
    }

    fun reload() {
        // Read resources
        var constructedToml = Toml()
        if (classPathName != null) {
            // There is a default configuration in the classpath, or so we hope.
            plugin.getPluginResourceAsInputStream(classPathName)?.use {
                constructedToml.read(it)
            }
            if (!file.isFile) {
                file.parentFile.mkdirs()
                plugin.exportResource(classPathName, file)
            }
        }
        if (file.isFile) {
            constructedToml = Toml(constructedToml).read(file)
        }
        this.toml = TomlKt(constructedToml)
    }

    fun write() {
        file.writeText(TOML_WRITER.write(toml.toml))
    }

    companion object {
        private val TOML_WRITER = TomlWriter.Builder()
            .padArrayDelimitersBy(1)
            .indentTablesBy(2)
            .timeZone(TimeZone.getTimeZone(ZoneOffset.UTC))
            .build()
    }
}