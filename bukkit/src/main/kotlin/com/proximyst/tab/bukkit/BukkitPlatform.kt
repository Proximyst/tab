package com.proximyst.tab.bukkit

import com.proximyst.tab.bukkit.ext.tabPlayer
import com.proximyst.tab.bukkit.platform.BukkitPlaceholderApi
import com.proximyst.tab.common.IPlatform
import org.bukkit.Server
import org.bukkit.entity.Player
import java.util.*

class BukkitPlatform(override val platform: Server) : IPlatform<Server, Player, BukkitPlayer, BukkitPlaceholderApi> {
    override val placeholderApi: BukkitPlaceholderApi? =
        if (platform.pluginManager.getPlugin("PlaceholderAPI") != null) BukkitPlaceholderApi()
        else null

    override val onlinePlayers: Collection<BukkitPlayer>
        get() = platform.onlinePlayers.map(Player::tabPlayer)

    override fun getPlayer(name: String): BukkitPlayer? =
        platform.getPlayerExact(name)?.let(Player::tabPlayer)

    override fun getPlayer(uuid: UUID): BukkitPlayer? =
        platform.getPlayer(uuid)?.let(Player::tabPlayer)
}