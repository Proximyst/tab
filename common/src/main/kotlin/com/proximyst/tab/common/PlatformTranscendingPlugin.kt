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

import com.proximyst.tab.common.config.commonvalues.HeaderFooterConfig
import com.proximyst.tab.common.handler.HeaderFooterHandler
import com.proximyst.tab.common.handler.PlayerListHandler
import com.proximyst.tab.common.model.TabGroup
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class PlatformTranscendingPlugin<
        InternalPlayer : Any,
        Player : ITabPlayer<InternalPlayer>,
        Server : Any,
        PlaceholderApi : IPlaceholderApi<Player>,
        Platform : IPlatform<Server, InternalPlayer, Player, PlaceholderApi>,
        Plugin : ITabPlatform<Platform>
        >(
    private val platformImpl: Plugin,
    configurationProvider: ConfigurationProvider
) {
    val playerCache = ConcurrentHashMap<UUID, Player>()
    val headerFooterHandler = HeaderFooterHandler(configurationProvider, platformImpl.platform.placeholderApi)
    val playerNameHandler = PlayerListHandler(configurationProvider, platformImpl.platform.placeholderApi)

    fun enable() {
        // Ensure all `ITabPlayer`s are created and ready.
        platformImpl.platform.onlinePlayers

        platformImpl.platform.onlinePlayers.forEach {
            headerFooterHandler.apply(it)
            playerNameHandler.apply(it)
        }
    }

    fun disable() {
        playerCache.forEach { (_, player) ->
            player.cleanup()
        }
    }

    fun refreshHeaderFooter() = platformImpl.platform.onlinePlayers.forEach(headerFooterHandler::apply)
    fun refreshPlayerNames() = platformImpl.platform.onlinePlayers.forEach(playerNameHandler::apply)

    fun joinedPlayer(player: Player) {
        // Should already be in the cache at this point.
        headerFooterHandler.apply(player)
        playerNameHandler.apply(player)
    }

    fun quitPlayer(player: Player) {
        player.cleanup()
        playerCache.remove(player.uniqueId)
    }

    interface ConfigurationProvider {
        val headerFooterConfig: HeaderFooterConfig
        val groups: List<TabGroup>
    }
}