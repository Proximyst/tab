/**
 * tab - A plugin for tab manipulation with ease in mind.
 * Copyright (C) 2020 Mariell Hoversholm
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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

/**
 * Implementation for [IPlaceholderApi] for the BungeeCord platform.
 *
 * This hooks into the plugin through the plugin messaging channel
 * `tab:placeholderapi` and sends [PlaceholderApiRequest]s to get the
 * placeholders of a player back in a [PlaceholderApiResponse], which gets
 * cached to [BungeePlayer.cachedPlaceholders].
 */
class BungeePlaceholderApi : IPlaceholderApi<BungeePlayer>, Listener {
    /**
     * A simple and non-lenient [Gson] instance.
     */
    private val gson = Gson()

    override fun replacePlaceholders(player: BungeePlayer, text: String): String {
        var result = text
        player.cachedPlaceholders.forEach { (placeholder, value) ->
            result = result.replace(placeholder, value)
        }
        refreshPlaceholders(player, text)
        return result
    }

    /**
     * Find all placeholders and request their current values.
     */
    private fun refreshPlaceholders(player: BungeePlayer, text: String) {
        val placeholders = mutableListOf<String>() // The found placeholders, with the per cent symbols.
        var position = 0 // The current position in the string.
        while (position < text.length && position >= 0) {
            position = text.indexOf('%', startIndex = position)
            if (position == -1) break
            val start = position
            position = text.indexOf('%', startIndex = position + 1) // +1 to make sure we don't go into an infinite loop
            if (position == -1) break
            val end = position
            position += 1 // Make sure we don't go into an infinite loop
            val substring = text.substring(start..end)
            if (substring.contains(' ')) continue
            placeholders.add(substring)
        }

        if (placeholders.isEmpty()) return // Nothing to request
        player.platformPlayer.server?.sendData(
            "tab:placeholderapi",
            gson.toJson(PlaceholderApiRequest(player.uniqueId, placeholders))
                .toByteArray()
        )
    }

    @EventHandler
    fun placeholderResponseReceived(event: PluginMessageEvent) {
        if (event.tag != "tab:placeholderapi") return

        // A response for a player came in!
        val json = String(event.data)
        val packet = gson.fromJson(json, PlaceholderApiResponse::class.java)

        val player = ProxyServer.getInstance().getPlayer(packet.uuid)?.tabPlayer ?: return // Not online (anymore?)

        // Update the responded placeholders.
        packet.placeholders.forEach { (placeholder, value) ->
            player.cachedPlaceholders[placeholder] = value
        }

        // Update the player's shown values now as the values have been changed.
        player.updateDisplayName()
        player.updateTabHeaderFooter()
    }
}