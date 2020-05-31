package com.proximyst.tab.bukkit

import com.proximyst.tab.bukkit.listener.TabPlayerListener
import com.proximyst.tab.bukkit.platform.BukkitPlaceholderApi
import com.proximyst.tab.common.ITabPlatform
import com.proximyst.tab.common.PlatformTranscendingPlugin
import com.proximyst.tab.common.config.TomlConfiguration
import com.proximyst.tab.common.config.commonvalues.GroupConfig
import com.proximyst.tab.common.config.commonvalues.HeaderFooterConfig
import com.proximyst.tab.common.config.config
import com.proximyst.tab.common.model.TabGroup
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
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
            BukkitPlaceholderApi,
            BukkitPlatform,
            TabPlugin
            >
        private set

    private val headerFooter: HeaderFooterConfig by config<HeaderFooterConfig>()
    private val groupSettings: GroupConfig by config<GroupConfig>()

    override fun onEnable() {
        tomlConfig = TomlConfiguration(this, File(dataFolder, "config.toml"), "config.toml")

        platformTranscendingPlugin = PlatformTranscendingPlugin(
            this,
            headerFooter,
            tomlConfig.toml.getTables("groups").map {
                @Suppress("RemoveExplicitTypeArguments") // kotlinc erred.
                it.to<TabGroup>()
            }
        )
        platformTranscendingPlugin.enable()

        server.pluginManager.registerEvents(TabPlayerListener(this), this)

        headerFooter.refreshInterval?.also { interval ->
            object : BukkitRunnable() {
                override fun run() {
                    platformTranscendingPlugin.refreshHeaderFooter()
                }
            }.runTaskTimerAsynchronously(this, interval, interval)
        }
        groupSettings.refreshInterval?.also { interval ->
            object : BukkitRunnable() {
                override fun run() {
                    platformTranscendingPlugin.refreshPlayerNames()
                }
            }.runTaskTimerAsynchronously(this, interval, interval)
        }
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