package com.proximyst.tab.common

import java.util.*

/**
 * A platform on which the plugin can run and retrieve data.
 */
interface IPlatform<Platform : Any, Player : Any> {
    /**
     * The platform object on the current platform.
     */
    val platform: Platform

    /**
     * All the current online players in an immutable collection.
     */
    val onlinePlayers: Collection<ITabPlayer<Player>>

    /**
     * Get an online player by their UUID.
     */
    fun getPlayer(uuid: UUID): ITabPlayer<Player>?

    /**
     * Get an online player by their exact name.
     */
    fun getPlayer(name: String): ITabPlayer<Player>?
}