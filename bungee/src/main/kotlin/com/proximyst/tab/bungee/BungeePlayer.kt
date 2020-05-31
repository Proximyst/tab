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
package com.proximyst.tab.bungee

import com.proximyst.tab.bungee.ext.toCreationPacket
import com.proximyst.tab.common.ITabPlayer
import net.kyori.text.Component
import net.kyori.text.TextComponent
import net.kyori.text.serializer.gson.GsonComponentSerializer
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.score.Team
import net.md_5.bungee.protocol.packet.Chat
import net.md_5.bungee.protocol.packet.PlayerListHeaderFooter
import net.md_5.bungee.protocol.packet.PlayerListItem
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import net.md_5.bungee.protocol.packet.Team as TeamPacket

class BungeePlayer(override val platformPlayer: ProxiedPlayer) : ITabPlayer<ProxiedPlayer> {
    companion object {
        private val orderTeams = ConcurrentHashMap<Int, Team>()

        private fun addPlayerToOrder(order: Int, playerName: String) {
            var hadToPut = false
            val team = orderTeams.computeIfAbsent(order) {
                hadToPut = true
                Team("TAB_ORD_${it.toString().padStart(5, '0')}")
            }
            team.addPlayer(playerName)

            val packet: TeamPacket
            if (hadToPut) {
                packet = team.toCreationPacket()
            } else {
                packet = TeamPacket()
                packet.mode = 3
                packet.players = arrayOf(playerName)
            }
            for (player in ProxyServer.getInstance().players) {
                player.unsafe().sendPacket(packet)
            }
        }

        private fun removePlayerFromOrder(order: Int, playerName: String) {
            val team = orderTeams[order] ?: return // No such team exists.
            team.removePlayer(playerName)
            val packet = TeamPacket()
            packet.name = team.name
            if (team.players.isEmpty()) {
                // Just nuke the team altogether.
                packet.mode = 1
            } else {
                // The player just left the team, no biggie.
                packet.mode = 4
                packet.players = arrayOf(playerName)
            }
            for (player in ProxyServer.getInstance().players) {
                player.unsafe().sendPacket(packet)
            }
        }
    }

    val cachedPlaceholders = ConcurrentHashMap<String, String>()

    override val isConnected: Boolean
        get() = platformPlayer.isConnected

    override val name: String
        get() = platformPlayer.name

    override val uniqueId: UUID
        get() = platformPlayer.uniqueId

    override val ping: Int
        get() = platformPlayer.ping

    override var playerListHeader: TextComponent? = null
        set(value) {
            field = value
            updateTabHeaderFooter()
        }

    override var playerListFooter: TextComponent? = null
        set(value) {
            field = value
            updateTabHeaderFooter()
        }

    override var playerListName: TextComponent = TextComponent.of(platformPlayer.name)
        set(value) {
            field = value.takeUnless { it.isEmpty } ?: TextComponent.of(name)
            updateDisplayName()
        }

    override var playerListPrefix: TextComponent? = null
        set(value) {
            field = value.takeUnless { it == null || it.isEmpty } ?: TextComponent.empty()
            updateDisplayName()
        }

    override var playerListSuffix: TextComponent? = null
        set(value) {
            field = value.takeUnless { it == null || it.isEmpty } ?: TextComponent.empty()
            updateDisplayName()
        }

    override var order: Int = 0
        set(value) {
            if (field != value) {
                removePlayerFromOrder(field, name)
                addPlayerToOrder(value, name)
            }
            field = value
        }

    override fun hasPermission(permission: String): Boolean =
        platformPlayer.hasPermission(permission)

    override fun sendMessage(text: Component) {
        platformPlayer.unsafe().sendPacket(Chat(GsonComponentSerializer.INSTANCE.serialize(text)))
    }

    fun sendOrderTeams() {
        orderTeams.forEach { (_, team) ->
            platformPlayer.unsafe().sendPacket(team.toCreationPacket())
        }
    }

    override fun cleanup() {
    }

    internal fun updateTabHeaderFooter() {
        platformPlayer.unsafe().sendPacket(PlayerListHeaderFooter().also {
            it.header = GsonComponentSerializer.INSTANCE
                .serialize(playerListHeader?.takeUnless { it.isEmpty } ?: TextComponent.empty())
            it.footer = GsonComponentSerializer.INSTANCE
                .serialize(playerListFooter?.takeUnless { it.isEmpty } ?: TextComponent.empty())
        })
    }

    internal fun updateDisplayName() {
        val name = TextComponent.make {
            val prefix = playerListPrefix
            if (prefix != null && !prefix.isEmpty)
                it.append(prefix)
            it.append(playerListName)
            val suffix = playerListSuffix
            if (suffix != null && !suffix.isEmpty)
                it.append(suffix)
        }
        val packet = PlayerListItem().also {
            it.action = PlayerListItem.Action.UPDATE_DISPLAY_NAME
            it.items = arrayOf(
                PlayerListItem.Item().also { pkt ->
                    pkt.uuid = uniqueId
                    pkt.displayName = GsonComponentSerializer.INSTANCE.serialize(name)
                }
            )
        }
        for (p in ProxyServer.getInstance().players) {
            p.unsafe().sendPacket(packet)
        }
    }
}