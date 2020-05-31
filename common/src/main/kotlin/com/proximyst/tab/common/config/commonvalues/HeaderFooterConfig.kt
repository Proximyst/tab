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
package com.proximyst.tab.common.config.commonvalues

/**
 * Configuration for the `[header-footer]` table.
 */
data class HeaderFooterConfig(
    /**
     * Whether the header & footer functionality should be used at all.
     */
    val enabled: Boolean,

    /**
     * How often the header & footer should refresh.
     * Calling refresh is done by the platforms.
     *
     * On Bukkit, this is in ticks.
     * On BungeeCord, this is in milliseconds.
     */
    val refreshInterval: Long?,

    /**
     * The header to display to players.
     *
     * This can have placeholders and colour codes with `&`.
     */
    val header: List<String>?,

    /**
     * The footer to display to players.
     *
     * This can have placeholders and colour codes with `&`.
     */
    val footer: List<String>?
)