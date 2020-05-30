package com.proximyst.tab.common

import com.proximyst.tab.common.config.commonvalues.HeaderFooterConfig
import com.proximyst.tab.common.handler.HeaderFooterHandler
import java.util.*

class PlatformTranscendingPlugin<
        InternalPlayer : Any,
        Player : ITabPlayer<InternalPlayer>,
        Server : Any,
        Platform : IPlatform<Server, InternalPlayer>,
        Plugin : ITabPlatform<Platform>
        >(
    private val platformImpl: Plugin,
    headerFooterConfig: HeaderFooterConfig
) {
    val playerCache = mutableMapOf<UUID, Player>()
    private val headerFooterHandler = HeaderFooterHandler(headerFooterConfig)

    fun enable() {
        // Ensure all `ITabPlayer`s are created and ready.
        platformImpl.platform.onlinePlayers

        platformImpl.platform.onlinePlayers.forEach(headerFooterHandler::apply)
    }

    fun disable() {
        playerCache.forEach { (_, player) ->
            player.cleanup()
        }
    }

    fun joinedPlayer(player: Player) {
        // Should already be in the cache at this point.
        headerFooterHandler.apply(player)
    }

    fun quitPlayer(player: Player) {
        player.cleanup()
        playerCache.remove(player.uniqueId)
    }
}