package com.proximyst.tab.bukkit

import com.proximyst.tab.bukkit.listener.TabPlayerListener
import com.proximyst.tab.bukkit.platform.BukkitPlaceholderApi
import com.proximyst.tab.common.ITabPlatform
import com.proximyst.tab.common.PlatformTranscendingPlugin
import com.proximyst.tab.common.config.TomlConfiguration
import com.proximyst.tab.common.config.commonvalues.GroupConfig
import com.proximyst.tab.common.config.commonvalues.HeaderFooterConfig
import com.proximyst.tab.common.config.commonvalues.PlaceholderApiConfig
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
    internal lateinit var groups: List<TabGroup>
    private val groupSettings: GroupConfig by config<GroupConfig>()
    private val placeholderApiSettings: PlaceholderApiConfig by config<PlaceholderApiConfig>("placeholderapi")

    override fun onEnable() {
        tomlConfig = TomlConfiguration(this, File(dataFolder, "config.toml"), "config.toml")

        if (placeholderApiSettings.pluginMessaging == true) {
            // The plugin should not function as usual!
            if (platform.placeholderApi == null) {
                logger.severe("Cannot enable plugin because PlaceholderAPI is not present and the plugin is configured for using it!")
                isEnabled = false
                return
            }
            server.messenger.registerIncomingPluginChannel(this, "tab:placeholderapi", platform.placeholderApi)
            server.messenger.registerOutgoingPluginChannel(this, "tab:placeholderapi")
            return
        }

        groups = tomlConfig.toml.getTables("groups").map {
            @Suppress("RemoveExplicitTypeArguments") // kotlinc erred.
            it.to<TabGroup>()
        }

        platformTranscendingPlugin = PlatformTranscendingPlugin(
            this,
            object : PlatformTranscendingPlugin.ConfigurationProvider {
                override val headerFooterConfig: HeaderFooterConfig
                    get() = this@TabPlugin.headerFooter
                override val groups: List<TabGroup>
                    get() = this@TabPlugin.groups
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
        if (placeholderApiSettings.pluginMessaging != true)
            platformTranscendingPlugin.disable()
    }

    override fun getPluginResourceAsInputStream(name: String): InputStream? =
        super.getResource(name)

    companion object {
        val instance: TabPlugin
            get() = getPlugin(TabPlugin::class.java)
    }
}