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
package com.proximyst.tab.bungee

import com.proximyst.tab.bungee.command.BTabCommand
import com.proximyst.tab.bungee.listener.TabPlayerListener
import com.proximyst.tab.common.EventHandler
import com.proximyst.tab.common.ITabPlatform
import com.proximyst.tab.common.config.TomlConfiguration
import com.proximyst.tab.common.config.commonvalues.GroupConfig
import com.proximyst.tab.common.config.commonvalues.HeaderFooterConfig
import com.proximyst.tab.common.config.commonvalues.PlaceholderApiConfig
import com.proximyst.tab.common.config.config
import com.proximyst.tab.common.model.TabGroup
import net.md_5.bungee.api.ProxyServer
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

    val eventHandler = EventHandler(this, { headerFooter }, { groups })

    internal var headerFooterConfigDelegate = config<HeaderFooterConfig>("header-footer")
    private val headerFooter: HeaderFooterConfig
        get() = headerFooterConfigDelegate.getValue(this, this::headerFooter)
    internal lateinit var groups: List<TabGroup>
    private val groupSettings: GroupConfig by config<GroupConfig>()
    internal val placeholderApiSettings: PlaceholderApiConfig by config<PlaceholderApiConfig>("placeholderapi")

    override fun onEnable() {
        tomlConfig = TomlConfiguration(this, File(dataFolder, "config.toml"), "config.toml")

        // Read groups as an array of tables (`[[groups]]`).
        // This cannot be done via delegates due to a limitation of my
        // implementation of delegated configuration values.
        groups = tomlConfig.toml.getTables("groups").map {
            @Suppress("RemoveExplicitTypeArguments") // kotlinc erred.
            it.to<TabGroup>()
        }

        eventHandler.enable()

        headerFooter.refreshInterval?.also { interval ->
            proxy.scheduler.schedule(
                this,
                {
                    platform.onlinePlayers.forEach(eventHandler::applyHeaderFooter)
                },
                interval,
                interval,
                TimeUnit.MILLISECONDS
            )
        }
        groupSettings.refreshInterval?.also { interval ->
            proxy.scheduler.schedule(
                this,
                {
                    platform.onlinePlayers.forEach(eventHandler::applyListGroup)
                },
                interval,
                interval,
                TimeUnit.MILLISECONDS
            )
        }

        if (platform.placeholderApi != null) {
            proxy.registerChannel("tab:placeholderapi")
            proxy.pluginManager.registerListener(this, platform.placeholderApi)
        }

        proxy.pluginManager.registerCommand(this, BTabCommand(this))
        proxy.pluginManager.registerListener(this, TabPlayerListener(this))
    }

    override fun onDisable() {
        proxy.pluginManager.unregisterListeners(this)
        proxy.pluginManager.unregisterCommands(this)
        eventHandler.disable()
    }

    override fun getPluginResourceAsInputStream(name: String): InputStream? =
        getResourceAsStream(name)

    companion object {
        lateinit var instance: TabPlugin
            private set
    }
}