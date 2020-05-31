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
import net.kyori.text.Component
import net.kyori.text.TextComponent
import net.kyori.text.serializer.gson.GsonComponentSerializer
import net.kyori.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class BukkitPlayer(override val platformPlayer: Player, private val main: TabPlugin) : ITabPlayer<Player> {
    companion object {
        internal val orderTeams = mutableMapOf<Int, Team>()
        private val lock = ReentrantLock()

        private fun createOrderTeam(order: Int): Team {
            return lock.withLock {
                orderTeams.computeIfAbsent(order) {
                    val name = "TAB_ORD_${it.toString().padStart(5, '0')}"
                    Bukkit.getScoreboardManager().mainScoreboard.getTeam(name)?.unregister()
                    Bukkit.getScoreboardManager().mainScoreboard.registerNewTeam(name)
                }
            }
        }
    }

    override fun cleanup() {
        val orderTeam = orderTeams[order]
        orderTeam?.removeEntry(name)
        if (orderTeam != null && orderTeam.entries.isEmpty()) {
            runCatching {
                orderTeam.unregister()
                orderTeams.remove(order)
            }.onFailure { it.printStackTrace() }
        }
    }

    override val isConnected: Boolean
        get() = platformPlayer.isOnline

    override val name: String
        get() = platformPlayer.name

    override val ping: Int
        get() = platformPlayer.spigot().ping

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

    override var order: Int = 0
        set(value) {
            if (value != field) {
                val oldTeam = orderTeams[field]
                oldTeam?.removeEntry(name)
                if (oldTeam != null && oldTeam.entries.isEmpty()) {
                    runCatching {
                        oldTeam.unregister()
                        orderTeams.remove(field)
                    }.onFailure { it.printStackTrace() }
                }
            }
            field = value
            val team = createOrderTeam(value)
            if (!team.hasEntry(name))
                team.addEntry(name)
        }

    override fun sendMessage(text: Component) =
        platformPlayer.sendRawMessage(GsonComponentSerializer.INSTANCE.serialize(text))

    override fun hasPermission(permission: String): Boolean =
        platformPlayer.hasPermission(permission)
}