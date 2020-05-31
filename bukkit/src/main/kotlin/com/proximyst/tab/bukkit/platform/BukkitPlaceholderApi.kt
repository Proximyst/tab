package com.proximyst.tab.bukkit.platform

import com.proximyst.tab.bukkit.BukkitPlayer
import com.proximyst.tab.common.IPlaceholderApi
import me.clip.placeholderapi.PlaceholderAPI

class BukkitPlaceholderApi : IPlaceholderApi<BukkitPlayer> {
    override fun replacePlaceholders(player: BukkitPlayer, text: String): String {
        return PlaceholderAPI.setPlaceholders(player.platformPlayer, text, false)
    }
}