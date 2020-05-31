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
package com.proximyst.tab.common.handler

import com.proximyst.tab.common.IPlaceholderApi
import com.proximyst.tab.common.ITabPlayer
import com.proximyst.tab.common.PlatformTranscendingPlugin
import com.proximyst.tab.common.ext.createComponentWithPlaceholders
import com.proximyst.tab.common.model.TabGroup
import net.kyori.text.TextComponent

/**
 * Handler for player list data sending and updating.
 *
 * This handles both sending of header & footer and placeholder replacements.
 */
class PlayerListHandler<Player : ITabPlayer<*>, PlaceholderApi : IPlaceholderApi<Player>>(
    /**
     * The provider for configuration values.
     *
     * This is required to be able to keep up with replacing the values at
     * runtime without much changes to the entire plugin setup.
     */
    private val configurationProvider: PlatformTranscendingPlugin.ConfigurationProvider,

    /**
     * The [PlaceholderApi] for this handler.
     *
     * If this is `null`, no placeholders will be replaced.
     */
    private val placeholderApi: PlaceholderApi?
) {
    /**
     * Apply the player list data to the given [Player] if it has any to be
     * applied.
     */
    fun apply(player: Player) {
        // Get the current configuration as a snapshot.
        // This ensures nothing becomes null under our noses.
        val groups = configurationProvider.groups

        // Make sure we only keep the groups whose permissions are actually
        // applied to our player.
        val applicableGroups = player.filterApplicableGroups(groups)

        // Make an "ultimate" group which includes all the properties the values
        // of other groups applied, with weight taken into account.
        val personalGroup = createPersonalGroup(applicableGroups)

        val prefix = personalGroup.prefix?.ifEmpty { null }?.createComponentWithPlaceholders(player, placeholderApi)
        val suffix = personalGroup.suffix?.ifEmpty { null }?.createComponentWithPlaceholders(player, placeholderApi)
        val name = personalGroup.playerName?.ifEmpty { null }
            ?.createComponentWithPlaceholders(player, placeholderApi) ?: TextComponent.of(player.name)

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
    private fun createPersonalGroup(groups: Collection<TabGroup>): TabGroup {
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