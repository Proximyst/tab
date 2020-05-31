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
package com.proximyst.tab.bukkit.platform

import com.google.gson.Gson
import com.proximyst.tab.bukkit.BukkitPlayer
import com.proximyst.tab.bukkit.TabPlugin
import com.proximyst.tab.common.IPlaceholderApi
import com.proximyst.tab.common.pluginmessage.PlaceholderApiRequest
import com.proximyst.tab.common.pluginmessage.PlaceholderApiResponse
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener

/**
 * Implementation for [IPlaceholderApi] for the Bukkit platform.
 *
 * This hooks into the plugin through [PlaceholderAPI] and refers to it.
 * This also handles plugin messages over the channel `tab:placeholderapi` of
 * type [PlaceholderApiRequest] and responds with [PlaceholderApiResponse]s.
 */
class BukkitPlaceholderApi : IPlaceholderApi<BukkitPlayer>, PluginMessageListener {
    /**
     * A simple and non-lenient [Gson] instance.
     */
    private val gson = Gson()

    override fun replacePlaceholders(player: BukkitPlayer, text: String): String {
        return PlaceholderAPI.setPlaceholders(player.platformPlayer, text, false)
    }

    override fun onPluginMessageReceived(channel: String, _ignored: Player, message: ByteArray) {
        if (channel != "tab:placeholderapi") return // Not ours to handle

        // A request for a player has come in!
        val json = String(message)
        val packet = gson.fromJson(json, PlaceholderApiRequest::class.java)

        val player = Bukkit.getPlayer(packet.uuid) ?: return // Not online (anymore?)

        val response = PlaceholderApiResponse(
            packet.uuid,
            packet.placeholders.mapNotNull {
                val value = PlaceholderAPI.setPlaceholders(
                    player,
                    it,
                    // A placeholder might return colour codes, which we don't want altered.
                    // We don't want them altered because we do so ourselves on the commons side
                    // and altering would force us to do twice the work for the same result.
                    false
                )
                if (value == it) return@mapNotNull null // There was no such placeholder; don't send extra.
                it to value
            }.toMap()
        )

        if (response.placeholders.isEmpty()) return // Nothing to send.

        player.sendPluginMessage(
            TabPlugin.instance,
            "tab:placeholderapi",
            gson.toJson(response).toByteArray()
        )
    }
}