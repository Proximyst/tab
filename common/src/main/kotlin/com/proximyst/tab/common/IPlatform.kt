package com.proximyst.tab.common

import java.util.*

/**
 * A platform on which the plugin can run and retrieve data.
 */
interface IPlatform<Platform : Any, PlatformPlayer : Any, Player : ITabPlayer<PlatformPlayer>, PlaceholderApi: IPlaceholderApi<Player>> {
    /**
     * The platform object on the current platform.
     */
    val platform: Platform

    /**
     * The placeholder API implementation for this platform.
     */
    val placeholderApi: PlaceholderApi?

    /**
     * All the current online players in an immutable collection.
     */
    val onlinePlayers: Collection<Player>

    /**
     * Get an online player by their UUID.
     */
    fun getPlayer(uuid: UUID): Player?

    /**
     * Get an online player by their exact name.
     */
    fun getPlayer(name: String): Player?
}