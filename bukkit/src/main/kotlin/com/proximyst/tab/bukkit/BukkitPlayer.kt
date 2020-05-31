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
package com.proximyst.tab.bukkit

import com.proximyst.tab.common.ITabPlayer
import net.kyori.text.TextComponent
import net.kyori.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team
import java.util.*

/**
 * The [ITabPlayer] implementation for Bukkit with its [Player].
 *
 * This handles all the behind-the-hood parts of a player on the Bukkit platform.
 */
class BukkitPlayer(override val platformPlayer: Player) : ITabPlayer<Player> {
    companion object {
        /**
         * The teams created for specific order levels.
         */
        private val orderTeams = mutableMapOf<Int, Team>()

        /**
         * Create a new order team or get one which already exists.
         *
         * The order should be in `[0, 99999]`.
         */
        private fun createOrderTeam(order: Int): Team {
            return orderTeams.computeIfAbsent(order) {
                val name = "TAB_ORD_${it.toString().padStart(5, '0')}"

                Bukkit.getScoreboardManager().mainScoreboard.let { board ->
                    // The team might already exist, so make sure it doesn't anymore.
                    board.getTeam(name)?.unregister()

                    // Create a new team now that we know there won't be an exception thrown.
                    board.registerNewTeam(name)
                }
            }
        }
    }

    override fun cleanup() {
        removeFromOrderTeam()
    }

    override val isConnected: Boolean
        get() = platformPlayer.isOnline

    override val name: String
        get() = platformPlayer.name

    override val uniqueId: UUID
        get() = platformPlayer.uniqueId

    override var playerListName: TextComponent
        get() = LegacyComponentSerializer.legacy().deserialize(platformPlayer.playerListName, ChatColor.COLOR_CHAR)
        set(value) = platformPlayer.setPlayerListName(
            if (value.isEmpty) null
            else LegacyComponentSerializer.legacy().serialize(value, ChatColor.COLOR_CHAR)
        )

    override var playerListPrefix: TextComponent? = null
        set(value) {
            field = value?.takeUnless { it.isEmpty } ?: TextComponent.empty()
        }

    override var playerListSuffix: TextComponent? = null
        set(value) {
            field = value?.takeUnless { it.isEmpty } ?: TextComponent.empty()
        }

    override var playerListHeader: TextComponent?
        get() = platformPlayer.playerListHeader?.ifEmpty { null }
            ?.let { LegacyComponentSerializer.legacy().deserialize(it, ChatColor.COLOR_CHAR) }
        set(value) {
            if (value != null && !value.isEmpty)
                platformPlayer.playerListHeader =
                    LegacyComponentSerializer.legacy().serialize(value, ChatColor.COLOR_CHAR)
            else
                platformPlayer.playerListHeader = null
        }

    override var playerListFooter: TextComponent?
        get() = platformPlayer.playerListFooter?.ifEmpty { null }
            ?.let { LegacyComponentSerializer.legacy().deserialize(it, ChatColor.COLOR_CHAR) }
        set(value) {
            if (value != null && !value.isEmpty)
                platformPlayer.playerListFooter =
                    LegacyComponentSerializer.legacy().serialize(value, ChatColor.COLOR_CHAR)
            else
                platformPlayer.playerListFooter = null
        }

    /**
     * Remove the player from the current order team.
     *
     * If the team ends up empty, also remove the team altogether.
     */
    private fun removeFromOrderTeam() {
        val ord = order
        val team = orderTeams[ord] ?: return
        team.removeEntry(name)
        if (team.entries.isEmpty()) {
            runCatching {
                team.unregister()
                orderTeams.remove(ord)
            }
        }
    }

    override var order: Int = 0
        set(value) {
            if (value != field) removeFromOrderTeam()
            field = value
            val team = createOrderTeam(value)
            if (!team.hasEntry(name))
                team.addEntry(name)
        }

    override fun hasPermission(permission: String): Boolean =
        platformPlayer.hasPermission(permission)
}