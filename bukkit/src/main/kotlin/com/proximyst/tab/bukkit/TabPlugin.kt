/**
 * tab - A plugin for tab manipulation with ease in mind.
 * Copyright (C) 2020 Mariell Hoversholm
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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

    /**
     * The [PlatformTranscendingPlugin] instance.
     *
     * This does not get set if [PlaceholderApiConfig.pluginMessaging] is true.
     */
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
            // It is hereby only a location where placeholders are parsed and handled.

            if (platform.placeholderApi == null) {
                logger.severe("Cannot enable plugin because PlaceholderAPI is not present and the plugin is configured for using it!")
                isEnabled = false
                return
            }

            server.messenger.registerIncomingPluginChannel(this, "tab:placeholderapi", platform.placeholderApi)
            server.messenger.registerOutgoingPluginChannel(this, "tab:placeholderapi")

            return
        }

        // Read groups as an array of tables (`[[groups]]`).
        // This cannot be done via delegates due to a limitation of my
        // implementation of delegated configuration values.
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
                    platformTranscendingPlugin.refreshPlayerListData()
                }
            }.runTaskTimerAsynchronously(this, interval, interval)
        }
    }

    override fun onDisable() {
        HandlerList.unregisterAll(this)
        if (placeholderApiSettings.pluginMessaging != true) // platformTranscendingPlugin is not set if this is true
            platformTranscendingPlugin.disable()
    }

    override fun getPluginResourceAsInputStream(name: String): InputStream? =
        super.getResource(name)

    companion object {
        val instance: TabPlugin
            get() = getPlugin(TabPlugin::class.java)
    }
}