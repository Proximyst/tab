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
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team
import java.util.*

class BukkitPlayer(override val platformPlayer: Player, private val main: TabPlugin) : ITabPlayer<Player> {
    private val team: Team = run {
        val teamName = "TAB_${uniqueId.hashCode()}"
        platformPlayer.scoreboard.getTeam(teamName)?.unregister() // Might already exist.
        platformPlayer.scoreboard.registerNewTeam(teamName).also {
            it.addEntry(name)
        }
    }

    override fun cleanup() {
        try {
            team.unregister()
        } catch (ignored: IllegalStateException) {
            main.logger.warning("The team for $name was already unregistered!")
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

    override var playerListPrefix: TextComponent?
        get() = team.prefix.ifEmpty { null }
            ?.let { LegacyComponentSerializer.legacy().deserialize(it, ChatColor.COLOR_CHAR) }
        set(value) {
            if (value != null && !value.isEmpty)
                team.prefix = LegacyComponentSerializer.legacy().serialize(value, ChatColor.COLOR_CHAR)
            else
                team.prefix = ""
        }

    override var playerListSuffix: TextComponent?
        get() = team.suffix.ifEmpty { null }
            ?.let { LegacyComponentSerializer.legacy().deserialize(it, ChatColor.COLOR_CHAR) }
        set(value) {
            if (value != null && !value.isEmpty)
                team.suffix = LegacyComponentSerializer.legacy().serialize(value, ChatColor.COLOR_CHAR)
            else
                team.suffix = ""
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

    override fun sendMessage(text: Component) =
        platformPlayer.sendRawMessage(GsonComponentSerializer.INSTANCE.serialize(text))

    override fun hasPermission(permission: String): Boolean =
        platformPlayer.hasPermission(permission)
}