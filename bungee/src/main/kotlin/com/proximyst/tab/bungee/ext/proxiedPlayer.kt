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
package com.proximyst.tab.bungee.ext

import com.proximyst.tab.bungee.BungeePlayer
import com.proximyst.tab.bungee.TabPlugin
import net.md_5.bungee.api.connection.ProxiedPlayer

/**
 * Get the [BungeePlayer] associated with this player, or create it if it isn't
 * already available.
 */
val ProxiedPlayer.tabPlayer: BungeePlayer
    get() = TabPlugin.instance.eventHandler.playerCache
        .computeIfAbsent(uniqueId) {
            BungeePlayer(this@tabPlayer)
        }