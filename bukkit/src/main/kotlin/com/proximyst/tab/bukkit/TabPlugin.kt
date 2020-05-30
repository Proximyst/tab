package com.proximyst.tab.bukkit

import com.proximyst.tab.common.ITabPlatform
import com.proximyst.tab.common.config.TomlConfiguration
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.InputStream

class TabPlugin : JavaPlugin(), ITabPlatform<BukkitPlatform> {
    override val dataDirectory: File
        get() = dataFolder

    override lateinit var tomlConfig: TomlConfiguration<ITabPlatform<BukkitPlatform>>
        private set

    override val platform = BukkitPlatform(Bukkit.getServer())

    override fun onEnable() {
        tomlConfig = TomlConfiguration(this, File(dataFolder, "config.toml"), "config.toml")
    }

    override fun getPluginResourceAsInputStream(name: String): InputStream? =
        super.getResource(name)

    companion object {
        val instance: TabPlugin
            get() = JavaPlugin.getPlugin(TabPlugin::class.java)
    }
}