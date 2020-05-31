package com.proximyst.tab.bungee.listener

import com.proximyst.tab.bungee.BungeePlayer
import com.proximyst.tab.bungee.TabPlugin
import com.proximyst.tab.bungee.ext.tabPlayer
import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class TabPlayerListener(private val main: TabPlugin): Listener {
    @EventHandler
    fun registerPlayer(event: PostLoginEvent) {
        main.platformTranscendingPlugin.joinedPlayer(
            main.platformTranscendingPlugin.playerCache.computeIfAbsent(event.player.uniqueId) {
                BungeePlayer(event.player)
            }
        )
    }

    @EventHandler
    fun unregisterPlayer(event: PlayerDisconnectEvent) {
        main.platformTranscendingPlugin.quitPlayer(event.player.tabPlayer)
    }
}