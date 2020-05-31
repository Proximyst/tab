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
package com.proximyst.tab.bungee.command

import com.proximyst.tab.bungee.TabPlugin
import com.proximyst.tab.common.config.commonvalues.HeaderFooterConfig
import com.proximyst.tab.common.config.config
import com.proximyst.tab.common.model.TabGroup
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.plugin.Command

class BTabCommand(private val main: TabPlugin) : Command("btab", "tab.btab", "bungee-tab", "bungeetab") {
    override fun execute(sender: CommandSender, args: Array<out String>) {
        if (args.isEmpty()) {
            syntax(sender)
            return
        }

        when (args.first().toLowerCase()) {
            "reload" -> reload(sender)
            else -> syntax(sender)
        }
    }

    private fun reload(sender: CommandSender) {
        main.tomlConfig.reload()
        main.headerFooterConfigDelegate = config<HeaderFooterConfig>("header-footer")
        main.groups = main.tomlConfig.toml.getTables("groups").map {
            @Suppress("RemoveExplicitTypeArguments") // kotlinc erred.
            it.to<TabGroup>()
        }
        sender.sendMessage(
            *ComponentBuilder("Tab has been reloaded!")
                .color(ChatColor.GREEN)
                .create()
        )
    }

    private fun syntax(sender: CommandSender) {
        sender.sendMessage(
            *ComponentBuilder("Syntax: /btab reload")
                .color(ChatColor.RED)
                .create()
        )
    }
}