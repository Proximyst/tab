package com.proximyst.tab.bukkit.ext

import com.proximyst.tab.bukkit.BukkitPlayer
import com.proximyst.tab.bukkit.TabPlugin
import org.bukkit.entity.Player

val Player.tabPlayer: BukkitPlayer
    get() = TabPlugin.instance.platformTranscendingPlugin.playerCache.computeIfAbsent(uniqueId) {
        BukkitPlayer(
            this@tabPlayer,
            TabPlugin.instance
        )
    }