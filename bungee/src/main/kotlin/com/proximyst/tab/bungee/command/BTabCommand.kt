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