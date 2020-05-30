package com.proximyst.tab.bukkit.listener

import com.proximyst.tab.bukkit.BukkitPlayer
import com.proximyst.tab.bukkit.TabPlugin
import com.proximyst.tab.bukkit.ext.tabPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class TabPlayerListener(private val main: TabPlugin) : Listener {
    @EventHandler
    fun registerPlayer(event: PlayerJoinEvent) {
        main.platformTranscendingPlugin.joinedPlayer(
            main.platformTranscendingPlugin.playerCache.computeIfAbsent(event.player.uniqueId) {
                BukkitPlayer(
                    event.player,
                    main
                )
            }
        )
    }

    @EventHandler
    fun unregisterPlayer(event: PlayerQuitEvent) {
        main.platformTranscendingPlugin.quitPlayer(event.player.tabPlayer)
    }
}