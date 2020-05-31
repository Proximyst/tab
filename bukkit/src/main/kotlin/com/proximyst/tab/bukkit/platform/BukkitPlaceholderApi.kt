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

class BukkitPlaceholderApi : IPlaceholderApi<BukkitPlayer>, PluginMessageListener {
    private val gson = Gson()

    override fun replacePlaceholders(player: BukkitPlayer, text: String): String {
        return PlaceholderAPI.setPlaceholders(player.platformPlayer, text, false)
    }

    override fun onPluginMessageReceived(channel: String, _ignored: Player, message: ByteArray) {
        if (channel != "tab:placeholderapi") return
        // A request for a player has come in!
        val json = String(message)
        val packet = gson.fromJson(json, PlaceholderApiRequest::class.java)
        val player = Bukkit.getPlayer(packet.uuid) ?: return // Not online (anymore?)
        val response = PlaceholderApiResponse(
            packet.uuid,
            packet.placeholders.map {
                it to PlaceholderAPI.setPlaceholders(player, it, false)
            }.toMap()
        )
        if (response.placeholders.isEmpty()) {
            return // Nothing to send.
        }
        player.sendPluginMessage(TabPlugin.instance, "tab:placeholderapi", gson.toJson(response).toByteArray())
    }
}