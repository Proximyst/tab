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
package com.proximyst.tab.common.model

/**
 * A group for players to be combed through in clusters.
 *
 * A player in a group will inherit all its properties by default, only overridden
 * by parenting groups and their own properties.
 */
data class TabGroup @JvmOverloads constructor(
    /**
     * The name of the group.
     *
     * This is used in the permissions node for this group.
     */
    val name: String,

    /**
     * The weight of the group.
     *
     * A higher weight means higher priority. If a group of weight 10 and 20 are
     * competing over a property, the group of weight 20 will always win. This can
     * be used and/or abused for one group wiping out a property previously set.
     */
    val weight: Int = 0,

    /**
     * The prefix to use for the group.
     */
    val prefix: String? = null,

    /**
     * The suffix to use for the group.
     */
    val suffix: String? = null,

    /**
     * The player's name in the player list.
     */
    val playerName: String? = null
) {
    val permission: String
        get() = "tab.groups.$name" // GSON does something funky.
}