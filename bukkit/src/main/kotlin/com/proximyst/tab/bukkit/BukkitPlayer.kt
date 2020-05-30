package com.proximyst.tab.bukkit

import com.proximyst.tab.bukkit.platform.TabTeam
import com.proximyst.tab.common.ITabPlayer
import net.kyori.text.Component
import net.kyori.text.TextComponent
import net.kyori.text.serializer.gson.GsonComponentSerializer
import net.kyori.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

class BukkitPlayer(override val platformPlayer: Player, private val main: TabPlugin) : ITabPlayer<Player> {
    private val team = TabTeam(this)

    internal fun cleanup() {
        try {
            team.unregister()
        } catch (ignored: IllegalStateException) {
            main.logger.warning("The team for $name was already unregistered!")
        }
    }

    override val isConnected: Boolean
        get() = platformPlayer.isOnline

    override val name: String
        get() = platformPlayer.name

    override val ping: Int
        get() = platformPlayer.spigot().ping

    override val uniqueId: UUID
        get() = platformPlayer.uniqueId

    override var playerListName: TextComponent
        get() = LegacyComponentSerializer.legacy().deserialize(platformPlayer.playerListName, ChatColor.COLOR_CHAR)
        set(value) = platformPlayer.setPlayerListName(
            if (value.isEmpty) null
            else LegacyComponentSerializer.legacy().serialize(value, ChatColor.COLOR_CHAR)
        )

    override var playerListPrefix: TextComponent?
        get() = team.prefix.ifEmpty { null }
            ?.let { LegacyComponentSerializer.legacy().deserialize(it, ChatColor.COLOR_CHAR) }
        set(value) {
            if (value?.isEmpty != false)
                team.prefix = ""
            else
                team.prefix = LegacyComponentSerializer.legacy().serialize(value, ChatColor.COLOR_CHAR)
        }

    override var playerListSuffix: TextComponent?
        get() = team.suffix.ifEmpty { null }
            ?.let { LegacyComponentSerializer.legacy().deserialize(it, ChatColor.COLOR_CHAR) }
        set(value) {
            if (value?.isEmpty != false)
                team.suffix = ""
            else
                team.suffix = LegacyComponentSerializer.legacy().serialize(value, ChatColor.COLOR_CHAR)
        }

    override var playerListHeader: TextComponent?
        get() = platformPlayer.playerListHeader?.ifEmpty { null }
            ?.let { LegacyComponentSerializer.legacy().deserialize(it, ChatColor.COLOR_CHAR) }
        set(value) {
            if (value?.isEmpty != false)
                platformPlayer.playerListHeader = null
            else
                platformPlayer.playerListHeader =
                    LegacyComponentSerializer.legacy().serialize(value, ChatColor.COLOR_CHAR)
        }

    override var playerListFooter: TextComponent?
        get() = platformPlayer.playerListFooter?.ifEmpty { null }
            ?.let { LegacyComponentSerializer.legacy().deserialize(it, ChatColor.COLOR_CHAR) }
        set(value) {
            if (value?.isEmpty != false)
                platformPlayer.playerListFooter = null
            else
                platformPlayer.playerListFooter =
                    LegacyComponentSerializer.legacy().serialize(value, ChatColor.COLOR_CHAR)
        }

    override fun sendMessage(text: Component) =
        platformPlayer.sendRawMessage(GsonComponentSerializer.INSTANCE.serialize(text))

    override fun hasPermission(permission: String): Boolean =
        platformPlayer.hasPermission(permission)
}