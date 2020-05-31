package com.proximyst.tab.bungee.ext

import com.proximyst.tab.bungee.BungeePlayer
import com.proximyst.tab.bungee.TabPlugin
import net.md_5.bungee.api.connection.ProxiedPlayer

val ProxiedPlayer.tabPlayer: BungeePlayer
    get() = TabPlugin.instance.platformTranscendingPlugin.playerCache.computeIfAbsent(uniqueId) {
        BungeePlayer(this@tabPlayer)
    }