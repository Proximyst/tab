/**
 * tab - A plugin for tab manipulation with ease in mind.
 * Copyright (C) 2020 Mariell Hoversholm
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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