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
package com.proximyst.tab.bukkit.listener

import com.proximyst.tab.bukkit.BukkitPlayer
import com.proximyst.tab.bukkit.TabPlugin
import com.proximyst.tab.bukkit.ext.tabPlayer
import com.proximyst.tab.common.ITabPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * Listener for handling joining and leaving [ITabPlayer]s.
 */
class TabPlayerListener(private val main: TabPlugin) : Listener {
    @EventHandler
    fun registerPlayer(event: PlayerJoinEvent) {
        main.eventHandler.playerConnected(BukkitPlayer(event.player))
    }

    @EventHandler
    fun unregisterPlayer(event: PlayerQuitEvent) {
        main.eventHandler.playerDisconnected(event.player.tabPlayer)
    }
}