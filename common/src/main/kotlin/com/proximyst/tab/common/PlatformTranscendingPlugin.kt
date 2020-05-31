package com.proximyst.tab.common

import com.proximyst.tab.common.config.commonvalues.HeaderFooterConfig
import com.proximyst.tab.common.handler.HeaderFooterHandler
import com.proximyst.tab.common.handler.PlayerNameHandler
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
    private val headerFooterHandler = HeaderFooterHandler(configurationProvider, platformImpl.platform.placeholderApi)
    private val playerNameHandler = PlayerNameHandler(configurationProvider, platformImpl.platform.placeholderApi)

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