package com.proximyst.tab.bungee

import com.proximyst.tab.bungee.ext.tabPlayer
import com.proximyst.tab.bungee.platform.BungeePlaceholderApi
import com.proximyst.tab.common.IPlatform
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*

class BungeePlatform(override val platform: ProxyServer) :
    IPlatform<ProxyServer, ProxiedPlayer, BungeePlayer, BungeePlaceholderApi> {
    override val placeholderApi: BungeePlaceholderApi? = null

    override val onlinePlayers: Collection<BungeePlayer>
        get() = platform.players.map(ProxiedPlayer::tabPlayer)

    override fun getPlayer(name: String): BungeePlayer? =
        platform.getPlayer(name)?.tabPlayer

    override fun getPlayer(uuid: UUID): BungeePlayer? =
        platform.getPlayer(uuid)?.tabPlayer
}