package com.proximyst.tab.bukkit

import com.proximyst.tab.bukkit.ext.tabPlayer
import com.proximyst.tab.common.IPlatform
import com.proximyst.tab.common.ITabPlayer
import org.bukkit.Server
import org.bukkit.entity.Player
import java.util.*

class BukkitPlatform(override val platform: Server) : IPlatform<Server, Player> {
    override val onlinePlayers: Collection<ITabPlayer<Player>>
        get() = platform.onlinePlayers.map(Player::tabPlayer)

    override fun getPlayer(name: String): ITabPlayer<Player>? =
        platform.getPlayerExact(name)?.let(Player::tabPlayer)

    override fun getPlayer(uuid: UUID): ITabPlayer<Player>? =
        platform.getPlayer(uuid)?.let(Player::tabPlayer)
}