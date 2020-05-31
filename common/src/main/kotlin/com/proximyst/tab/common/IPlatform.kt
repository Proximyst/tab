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

import java.util.*

/**
 * A platform on which the plugin can run and retrieve data.
 */
interface IPlatform<Platform : Any, PlatformPlayer : Any, Player : ITabPlayer<PlatformPlayer>, PlaceholderApi: IPlaceholderApi<Player>> {
    /**
     * The platform object on the current platform.
     */
    val platform: Platform

    /**
     * The placeholder API implementation for this platform.
     */
    val placeholderApi: PlaceholderApi?

    /**
     * All the current online players in an immutable collection.
     */
    val onlinePlayers: Collection<Player>

    /**
     * Get an online player by their UUID.
     */
    fun getPlayer(uuid: UUID): Player?

    /**
     * Get an online player by their exact name.
     */
    fun getPlayer(name: String): Player?
}