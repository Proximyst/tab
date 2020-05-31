package com.proximyst.tab.common.config.commonvalues

data class GroupConfig(
    /**
     * How often the player groups should refresh.
     * Calling refresh is done by the platforms.
     *
     * On Bukkit, this is in ticks.
     * On BungeeCord, this is in milliseconds.
     */
    val refreshInterval: Long?
)