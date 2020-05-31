package com.proximyst.tab.bungee.platform

import com.proximyst.tab.bungee.BungeePlayer
import com.proximyst.tab.common.IPlaceholderApi

class BungeePlaceholderApi : IPlaceholderApi<BungeePlayer> {
    override fun replacePlaceholders(player: BungeePlayer, text: String): String {
        // TODO(Proximyst): Plugin messaging channel for placeholders
        return text
    }
}