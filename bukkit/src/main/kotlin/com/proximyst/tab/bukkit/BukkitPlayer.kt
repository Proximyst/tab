package com.proximyst.tab.bukkit

import com.proximyst.tab.common.ITabPlayer
import net.kyori.text.Component
import net.kyori.text.serializer.gson.GsonComponentSerializer
import org.bukkit.entity.Player
import java.util.*

class BukkitPlayer(override val platformPlayer: Player) : ITabPlayer<Player> {
    override val isConnected: Boolean
        get() = platformPlayer.isOnline

    override val name: String
        get() = platformPlayer.name

    override val ping: Int
        get() = platformPlayer.spigot().ping

    override val uniqueId: UUID
        get() = platformPlayer.uniqueId

    override var playerListName: String
        get() = platformPlayer.playerListName
        set(value) = platformPlayer.setPlayerListName(value.ifEmpty { null })

    override fun sendMessage(text: Component) =
        platformPlayer.sendRawMessage(GsonComponentSerializer.INSTANCE.serialize(text))

    override fun hasPermission(permission: String): Boolean =
        platformPlayer.hasPermission(permission)
}