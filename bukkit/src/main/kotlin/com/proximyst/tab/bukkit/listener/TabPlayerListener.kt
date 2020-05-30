package com.proximyst.tab.bukkit.listener

import com.proximyst.tab.bukkit.BukkitPlayer
import com.proximyst.tab.bukkit.TabPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class TabPlayerListener(private val main: TabPlugin) : Listener {
    @EventHandler
    fun registerPlayer(event: PlayerJoinEvent) {
        main.tabPlayers[event.player] = BukkitPlayer(event.player, main)
    }

    @EventHandler
    fun unregisterPlayer(event: PlayerQuitEvent) {
        main.tabPlayers.remove(event.player)?.cleanup()
    }
}