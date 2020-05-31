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
package com.proximyst.tab.common

import com.proximyst.tab.common.config.TomlConfiguration
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Common interface between the plugins of each platform.
 */
interface ITabPlatform<P: IPlatform<*, *, *, *>> {
    /**
     * The current platform.
     */
    val platform: P

    /**
     * The [TomlConfiguration] for this platform.
     */
    val tomlConfig: TomlConfiguration<ITabPlatform<P>>

    /**
     * The data directory of this platform's plugin.
     */
    val dataDirectory: File

    /**
     * Export a resource to the data directory of this platform's plugin.
     *
     * @param name The name of the file in the jar.
     * @param destination The destination file where the file will be exported to.
     * @param overwrite Should the file be overwritten if it already exists?
     * @return Whether the export was a success.
     * @throws IOException An error was encountered during the handling of the destination file.
     */
    @Throws(IOException::class)
    fun exportResource(name: String, destination: File, overwrite: Boolean = false): Boolean {
        destination.parentFile.mkdirs()

        if (!overwrite && destination.isFile) return false
        if (destination.isDirectory) {
            // Interesting - let's fail here.
            throw IllegalStateException("there is already a directory at ${destination.absolutePath}")
        }

        getPluginResourceAsInputStream(name)?.use {
            Files.copy(
                it,
                destination.toPath(),
                StandardCopyOption.REPLACE_EXISTING
            )
        } ?: run {
            throw IllegalArgumentException("no filename in classpath: $name")
        }

        return true
    }

    /**
     * Get a resource from the jar of the plugin.
     *
     * @return The resource or `null` if none was found.
     */
    fun getPluginResourceAsInputStream(name: String): InputStream?
}