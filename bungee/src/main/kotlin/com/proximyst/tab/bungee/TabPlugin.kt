package com.proximyst.tab.bungee

import com.proximyst.tab.bungee.platform.BungeePlaceholderApi
import com.proximyst.tab.common.ITabPlatform
import com.proximyst.tab.common.PlatformTranscendingPlugin
import com.proximyst.tab.common.config.TomlConfiguration
import com.proximyst.tab.common.config.commonvalues.GroupConfig
import com.proximyst.tab.common.config.commonvalues.HeaderFooterConfig
import com.proximyst.tab.common.config.config
import com.proximyst.tab.common.model.TabGroup
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Plugin
import java.io.File
import java.io.InputStream
import java.util.concurrent.TimeUnit

class TabPlugin : Plugin(), ITabPlatform<BungeePlatform> {
    override val dataDirectory: File
        get() = dataFolder
    override lateinit var tomlConfig: TomlConfiguration<ITabPlatform<BungeePlatform>>
        private set
    override val platform = BungeePlatform(ProxyServer.getInstance())

    init {
        instance = this
    }

    private val headerFooter: HeaderFooterConfig by config<HeaderFooterConfig>()
    private val groupSettings: GroupConfig by config<GroupConfig>()

    lateinit var platformTranscendingPlugin: PlatformTranscendingPlugin<
            ProxiedPlayer,
            BungeePlayer,
            ProxyServer,
            BungeePlaceholderApi,
            BungeePlatform,
            TabPlugin
            >

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

        headerFooter.refreshInterval?.also { interval ->
            proxy.scheduler.schedule(
                this,
                platformTranscendingPlugin::refreshHeaderFooter,
                interval,
                interval,
                TimeUnit.MILLISECONDS
            )
        }
        groupSettings.refreshInterval?.also { interval ->
            proxy.scheduler.schedule(
                this,
                platformTranscendingPlugin::refreshPlayerNames,
                interval,
                interval,
                TimeUnit.MILLISECONDS
            )
        }
    }

    override fun onDisable() {
        proxy.pluginManager.unregisterListeners(this)
        platformTranscendingPlugin.disable()
    }

    override fun getPluginResourceAsInputStream(name: String): InputStream? =
        getResourceAsStream(name)

    companion object {
        lateinit var instance: TabPlugin
            private set
    }
}