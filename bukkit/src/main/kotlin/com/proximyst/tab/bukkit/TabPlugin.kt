package com.proximyst.tab.bukkit

import com.proximyst.tab.bukkit.listener.TabPlayerListener
import com.proximyst.tab.common.ITabPlatform
import com.proximyst.tab.common.config.TomlConfiguration
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.InputStream

class TabPlugin : JavaPlugin(), ITabPlatform<BukkitPlatform> {
    val tabPlayers = mutableMapOf<Player, BukkitPlayer>()

    override val dataDirectory: File
        get() = dataFolder

    override lateinit var tomlConfig: TomlConfiguration<ITabPlatform<BukkitPlatform>>
        private set

    override val platform = BukkitPlatform(Bukkit.getServer(), this)

    override fun onEnable() {
        tomlConfig = TomlConfiguration(this, File(dataFolder, "config.toml"), "config.toml")
        server.pluginManager.registerEvents(TabPlayerListener(this), this)
    }

    override fun onDisable() {
        HandlerList.unregisterAll(this)
    }

    override fun getPluginResourceAsInputStream(name: String): InputStream? =
        super.getResource(name)

    companion object {
        val instance: TabPlugin
            get() = getPlugin(TabPlugin::class.java)
    }
}