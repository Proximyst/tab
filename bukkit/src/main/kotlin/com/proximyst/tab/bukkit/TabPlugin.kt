package com.proximyst.tab.bukkit

import com.proximyst.tab.bukkit.listener.TabPlayerListener
import com.proximyst.tab.common.ITabPlatform
import com.proximyst.tab.common.PlatformTranscendingPlugin
import com.proximyst.tab.common.config.TomlConfiguration
import com.proximyst.tab.common.config.commonvalues.HeaderFooterConfig
import com.proximyst.tab.common.config.config
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.InputStream

class TabPlugin : JavaPlugin(), ITabPlatform<BukkitPlatform> {
    override val dataDirectory: File
        get() = dataFolder
    override lateinit var tomlConfig: TomlConfiguration<ITabPlatform<BukkitPlatform>>
        private set
    override val platform = BukkitPlatform(Bukkit.getServer())

    lateinit var platformTranscendingPlugin: PlatformTranscendingPlugin<
            Player,
            BukkitPlayer,
            Server,
            BukkitPlatform,
            TabPlugin
            >
        private set

    private val headerFooter: HeaderFooterConfig by config<HeaderFooterConfig>()

    override fun onEnable() {
        tomlConfig = TomlConfiguration(this, File(dataFolder, "config.toml"), "config.toml")

        platformTranscendingPlugin = PlatformTranscendingPlugin(
            this,
            headerFooter
        )
        platformTranscendingPlugin.enable()

        server.pluginManager.registerEvents(TabPlayerListener(this), this)
    }

    override fun onDisable() {
        HandlerList.unregisterAll(this)
        platformTranscendingPlugin.disable()
    }

    override fun getPluginResourceAsInputStream(name: String): InputStream? =
        super.getResource(name)

    companion object {
        val instance: TabPlugin
            get() = getPlugin(TabPlugin::class.java)
    }
}