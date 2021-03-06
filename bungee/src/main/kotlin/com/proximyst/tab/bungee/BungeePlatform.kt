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
package com.proximyst.tab.bungee

import com.proximyst.tab.bungee.ext.tabPlayer
import com.proximyst.tab.bungee.platform.BungeePlaceholderApi
import com.proximyst.tab.common.IPlatform
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*

/**
 * The [IPlatform] implementation for BungeeCord.
 *
 * This provides [BungeePlayer]s and the [BungeePlaceholderApi] for use in
 * common, platform-less code.
 */
class BungeePlatform(override val platform: ProxyServer) :
    IPlatform<ProxyServer, ProxiedPlayer, BungeePlayer, BungeePlaceholderApi> {
    // This must not be handled upon instantiation of the main plugin class,
    // therefore a lazy works fine as a hack.
    private val placeholderApiDelegate by lazy {
        if (TabPlugin.instance.placeholderApiSettings.pluginMessaging == true) BungeePlaceholderApi()
        else null
    }
    override val placeholderApi: BungeePlaceholderApi?
        get() = placeholderApiDelegate

    override val onlinePlayers: Collection<BungeePlayer>
        get() = platform.players.map(ProxiedPlayer::tabPlayer)

    override fun getPlayer(name: String): BungeePlayer? =
        platform.getPlayer(name)?.tabPlayer

    override fun getPlayer(uuid: UUID): BungeePlayer? =
        platform.getPlayer(uuid)?.tabPlayer
}