package com.proximyst.tab.bukkit.ext

import com.proximyst.tab.bukkit.BukkitPlayer
import com.proximyst.tab.common.ITabPlayer
import org.bukkit.entity.Player

val Player.tabPlayer: ITabPlayer<Player>
    get() = BukkitPlayer(this)