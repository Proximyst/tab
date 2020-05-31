package com.proximyst.tab.common.config.commonvalues

data class HeaderFooterConfig(
    val enabled: Boolean,
    /**
     * How often the header & footer should refresh.
     * Calling refresh is done by the platforms.
     *
     * On Bukkit, this is in ticks.
     * On BungeeCord, this is in milliseconds.
     */
    val refreshInterval: Long?,
    val header: List<String>?,
    val footer: List<String>?
)