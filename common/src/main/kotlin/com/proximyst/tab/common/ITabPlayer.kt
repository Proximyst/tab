package com.proximyst.tab.common

import net.kyori.text.Component
import net.kyori.text.TextComponent
import java.util.*

interface ITabPlayer<P : Any> {
    /**
     * The player object on the current platform.
     */
    val platformPlayer: P

    /**
     * The name of the player.
     */
    val name: String

    /**
     * The [UUID] of the player.
     */
    val uniqueId: UUID

    /**
     * Whether the player is currently connected.
     */
    val isConnected: Boolean

    /**
     * The player's current ping as known by the platform.
     */
    val ping: Int

    /**
     * The name of the player in the player list.
     */
    var playerListName: String

    /**
     * Send a message to the player.
     *
     * @param text The text to send to the player.
     */
    fun sendMessage(text: String) = sendMessage(TextComponent.of(text))

    /**
     * Send a message to the player.
     *
     * @param text The text to send to the player. This is completely untouched.
     */
    fun sendMessage(text: Component)

    /**
     * Check whether the player has a permission node.
     *
     * @param permission The permission to check whether the player has.
     */
    fun hasPermission(permission: String): Boolean
}