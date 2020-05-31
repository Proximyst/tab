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

/**
 * A plugin which transcends any plugin lines and borders.
 */
class PlatformTranscendingPlugin<
        /**
         * The internal player type for this platform.
         */
        InternalPlayer : Any,

        /**
         * The platform-less player implementation for this platform.
         */
        Player : ITabPlayer<InternalPlayer>,

        /**
         * The internal server type for this platform.
         */
        Server : Any,

        /**
         * The platform-less placeholder API implementation for this platform.
         */
        PlaceholderApi : IPlaceholderApi<Player>,

        /**
         * The platform-less representation of the platform this plugin is
         * running on.
         */
        Platform : IPlatform<Server, InternalPlayer, Player, PlaceholderApi>,

        /**
         * The internal plugin type for the platform this plugin is running on.
         *
         * This implementation has the job of making sure this plugin is handled
         * properly and correctly.
         */
        Plugin : ITabPlatform<Platform>
        >(
    /**
     * The implementation instance of the platform-specific plugin.
     */
    private val platformImpl: Plugin,

    /**
     * The provider for configuration values.
     *
     * This is required to be able to keep up with replacing the values at
     * runtime without much changes to the entire plugin setup.
     */
    configurationProvider: ConfigurationProvider
) {
    /**
     * The cache of players mapped by their [UUID]s.
     *
     * This is a concurrent map to let modifications happen on any thread, as
     * not all implementations may have an idea of a "main" thread.
     */
    val playerCache = ConcurrentHashMap<UUID, Player>()

    /**
     * The handler for header and footers without regard to platform.
     */
    val headerFooterHandler = HeaderFooterHandler(configurationProvider, platformImpl.platform.placeholderApi)

    /**
     * The handler for player list data without regard to platform.
     */
    val playerListHandler = PlayerListHandler(configurationProvider, platformImpl.platform.placeholderApi)

    fun enable() {
        // Ensure all `ITabPlayer`s are created and ready.
        platformImpl.platform.onlinePlayers

        // Update all the existing players in case this plugin was loaded at runtime.
        platformImpl.platform.onlinePlayers.forEach {
            headerFooterHandler.apply(it)
            playerListHandler.apply(it)
        }
    }

    fun disable() {
        // Make sure all players have their data cleaned up.
        playerCache.forEach { (_, player) ->
            player.cleanup()
        }
    }

    /**
     * Refresh all players' headers and footers in the player list.
     */
    fun refreshHeaderFooter() = platformImpl.platform.onlinePlayers.forEach(headerFooterHandler::apply)

    /**
     * Refresh all players' data in the player list.
     */
    fun refreshPlayerListData() = platformImpl.platform.onlinePlayers.forEach(playerListHandler::apply)

    /**
     * Handle a player which has just joined the server.
     */
    fun joinedPlayer(player: Player) {
        // Should already be in the cache at this point.
        headerFooterHandler.apply(player)
        playerListHandler.apply(player)
    }

    /**
     * Handle a player who has just disconnected from the player.
     */
    fun disconnectedPlayer(player: Player) {
        player.cleanup()
        playerCache.remove(player.uniqueId)
    }

    /**
     * A provider for configuration values.
     *
     * This is required to be able to keep up with replacing the values at
     * runtime without much changes to the entire plugin setup.
     */
    interface ConfigurationProvider {
        /**
         * The configuration for how headers and footers should behave.
         */
        val headerFooterConfig: HeaderFooterConfig

        /**
         * The list of [TabGroup]s to display and handle in the player list.
         */
        val groups: List<TabGroup>
    }
}