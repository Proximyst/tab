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

import com.proximyst.tab.common.model.TabGroup
import net.kyori.text.Component
import net.kyori.text.TextComponent
import java.util.*

interface ITabPlayer<P : Any> {
    /**
     * The player object on the current platform.
     */
    val platformPlayer: P

    /**
     * The name of the player.
     */
    val name: String

    /**
     * The [UUID] of the player.
     */
    val uniqueId: UUID

    /**
     * Whether the player is currently connected.
     */
    val isConnected: Boolean

    /**
     * The player's current ping as known by the platform.
     */
    val ping: Int

    /**
     * The name of the player in the player list.
     */
    var playerListName: TextComponent

    /**
     * The prefix of the player's listing in the player list.
     */
    var playerListPrefix: TextComponent?

    /**
     * The suffix of the player's listing in the player list.
     */
    var playerListSuffix: TextComponent?

    /**
     * The header in the player list.
     */
    var playerListHeader: TextComponent?

    /**
     * The footer in the player list.
     */
    var playerListFooter: TextComponent?

    /**
     * Send a message to the player.
     *
     * @param text The text to send to the player.
     */
    fun sendMessage(text: String) = sendMessage(TextComponent.of(text))

    /**
     * Send a message to the player.
     *
     * @param text The text to send to the player. This is completely untouched.
     */
    fun sendMessage(text: Component)

    /**
     * Check whether the player has a permission node.
     *
     * @param permission The permission to check whether the player has.
     */
    fun hasPermission(permission: String): Boolean

    /**
     * Do necessary cleanup for this player.
     */
    fun cleanup()

    /**
     * Return which groups are applicable from a list of groups.
     */
    fun filterApplicableGroups(groups: Collection<TabGroup>): List<TabGroup> =
        groups.filter { hasPermission(it.permission) }
}