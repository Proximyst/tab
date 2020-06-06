package com.proximyst.tab.common

import com.proximyst.tab.common.config.commonvalues.HeaderFooterConfig
import com.proximyst.tab.common.ext.createComponentWithPlaceholders
import com.proximyst.tab.common.model.TabGroup
import net.kyori.text.TextComponent
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class EventHandler<
        /**
         * The internal player type for this platform.
         */
        InternalPlayer : Any,

        /**
         * The platform-less player implementation for this platform.
         */
        Player : ITabPlayer<InternalPlayer>,

        /**
         * The internal server type for this platform.
         */
        Server : Any,

        /**
         * The platform-less placeholder API implementation for this platform.
         */
        PlaceholderApi : IPlaceholderApi<Player>,

        /**
         * The platform-less representation of the platform this plugin is
         * running on.
         */
        Platform : IPlatform<Server, InternalPlayer, Player, PlaceholderApi>,

        /**
         * The internal plugin type for the platform this plugin is running on.
         *
         * This implementation has the job of making sure this plugin is handled
         * properly and correctly.
         */
        Plugin : ITabPlatform<Platform>
        >(
    private val plugin: Plugin,
    private val headerFooterConfigGetter: () -> HeaderFooterConfig,
    private val groupsGetter: () -> List<TabGroup>
) {
    /**
     * The cache of players mapped by their [UUID]s.
     *
     * This is a concurrent map to let modifications happen on any thread, as
     * not all implementations may have an idea of a "main" thread.
     */
    val playerCache = ConcurrentHashMap<UUID, Player>()

    fun enable() {
        // Ensure all `ITabPlayer`s are created and ready.
        plugin.platform.onlinePlayers

        // Update all the existing players in case this plugin was loaded at runtime.
        plugin.platform.onlinePlayers.forEach {
            applyHeaderFooter(it)
            applyListGroup(it)
        }
    }

    fun disable() {
        // Make sure all players have their data cleaned up.
        playerCache.forEach { (_, player) ->
            player.cleanup()
        }
    }

    /**
     * Handle a player which has just connected to the server.
     */
    fun playerConnected(player: Player) {
        playerCache[player.uniqueId] = player
        applyHeaderFooter(player)
        applyListGroup(player)
    }

    /**
     * Handle a player who has just disconnected from the server.
     */
    fun playerDisconnected(player: Player) {
        player.cleanup()
        playerCache.remove(player.uniqueId)
    }

    fun createComponentList(player: Player, list: List<String>?) =
        list?.ifEmpty { null }
            ?.map { it.createComponentWithPlaceholders(player, plugin.platform.placeholderApi) }
            ?.let { components ->
                // We now have separate lines, so we'll have to create a new
                // component with the lines separated with actual new line
                // separators.
                TextComponent.make { builder ->
                    components.forEachIndexed { idx, component ->
                        builder.append(component)
                        if (idx != components.size - 1)
                            builder.append(TextComponent.newline())
                                // A new line is a new component, so it should not inherit style
                                .resetStyle()
                    }
                }
            }

    fun applyHeaderFooter(player: Player) {
        val config = headerFooterConfigGetter()
        if (!config.enabled) return

        createComponentList(player, config.header)?.let { player.playerListHeader = it }
        createComponentList(player, config.footer)?.let { player.playerListFooter = it }
    }

    fun applyListGroup(player: Player) {
        // Get the current configuration as a snapshot.
        // This ensures nothing becomes null under our noses.
        val groups = groupsGetter()
        if (groups.isEmpty()) return

        // Make sure we only keep the groups whose permissions are actually
        // applied to our player.
        val applicableGroups = player.filterApplicableGroups(groups)

        // Make an "ultimate" group which includes all the properties the values
        // of other groups applied, with weight taken into account.
        val personalGroup = deducePersonalGroup(applicableGroups)

        val prefix = personalGroup.prefix?.ifEmpty { null }
            ?.createComponentWithPlaceholders(player, plugin.platform.placeholderApi)
        val suffix = personalGroup.suffix?.ifEmpty { null }
            ?.createComponentWithPlaceholders(player, plugin.platform.placeholderApi)
        val name = personalGroup.playerName?.ifEmpty { null }
            ?.replace("{name}", player.name) // Built-in placeholder
            ?.createComponentWithPlaceholders(player, plugin.platform.placeholderApi) ?: TextComponent.of(player.name)

        // We don't want to set the player list name if there is no data to set.
        if (prefix?.isEmpty == false || suffix?.isEmpty == false)
            player.playerListName = TextComponent.make {
                if (prefix != null) it.append(prefix)
                it.append(name)
                if (suffix != null) it.append(suffix)
            }
        else
            player.playerListName = name

        val order = personalGroup.order ?: 0
        if (player.order != order)
            player.order = order
    }

    /**
     * Create a [TabGroup] with the data of other [TabGroup]s applied to it when
     * weight is taken into account, such that we only have values which are
     * actually applicable to the player in a sensible manner.
     */
    fun deducePersonalGroup(groups: Collection<TabGroup>): TabGroup {
        var group = TabGroup("", 0)
        for (g in groups.sortedByDescending { it.weight }) {
            if (group.prefix == null && g.prefix != null)
                group = group.copy(prefix = g.prefix)

            if (group.suffix == null && g.suffix != null)
                group = group.copy(suffix = g.suffix)

            if (group.playerName == null && g.playerName != null)
                group = group.copy(playerName = g.playerName)

            if (group.order == null && g.order != null)
                group = group.copy(order = g.order)
        }
        return group
    }
}