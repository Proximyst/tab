package com.proximyst.tab.bungee.platform

import com.google.gson.Gson
import com.proximyst.tab.bungee.BungeePlayer
import com.proximyst.tab.bungee.ext.tabPlayer
import com.proximyst.tab.common.IPlaceholderApi
import com.proximyst.tab.common.pluginmessage.PlaceholderApiRequest
import com.proximyst.tab.common.pluginmessage.PlaceholderApiResponse
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.event.PluginMessageEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class BungeePlaceholderApi : IPlaceholderApi<BungeePlayer>, Listener {
    private val gson = Gson()

    override fun replacePlaceholders(player: BungeePlayer, text: String): String {
        var result = text
        player.cachedPlaceholders.forEach { (placeholder, value) ->
            result = result.replace(placeholder, value)
        }
        refreshPlaceholders(player, text)
        return result
    }

    private fun refreshPlaceholders(player: BungeePlayer, text: String) {
        val placeholders = mutableListOf<String>()
        var position = 0
        while (position < text.length && position >= 0) {
            position = text.indexOf('%', startIndex = position)
            if (position == -1) break
            val start = position
            position = text.indexOf('%', startIndex = position + 1)
            if (position == -1) break
            val end = position
            position += 1
            val substring = text.substring(start..end)
            if (substring.contains(' ')) continue
            placeholders.add(substring)
        }

        if (placeholders.isEmpty()) return // Nothing to request
        val packet = PlaceholderApiRequest(player.uniqueId, placeholders)
        player.platformPlayer.server?.sendData("tab:placeholderapi", gson.toJson(packet).toByteArray())
    }

    @EventHandler
    fun placeholderResponseReceived(event: PluginMessageEvent) {
        if (event.tag != "tab:placeholderapi") return
        // A response for a player came in!
        val json = String(event.data)
        val packet = gson.fromJson(json, PlaceholderApiResponse::class.java)
        val player = ProxyServer.getInstance().getPlayer(packet.uuid)?.tabPlayer ?: return // Not online (anymore?)
        packet.placeholders.forEach { (placeholder, value) ->
            player.cachedPlaceholders[placeholder] = value
        }

        // Update the player's shown values now.
        player.updateDisplayName()
        player.updateTabHeaderFooter()
    }
}