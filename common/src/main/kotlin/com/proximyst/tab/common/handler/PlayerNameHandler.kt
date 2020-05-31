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

class PlayerNameHandler<Player : ITabPlayer<*>, PlaceholderApi : IPlaceholderApi<Player>>(
    private val configurationProvider: PlatformTranscendingPlugin.ConfigurationProvider,
    private val placeholderApi: PlaceholderApi?
) {
    fun apply(player: Player) {
        val groups = configurationProvider.groups
        val applicableGroups = player.filterApplicableGroups(groups)
        val personalGroup = createPersonalGroup(applicableGroups)

        val prefix = personalGroup.prefix?.ifEmpty { null }?.createComponentWithPlaceholders(player, placeholderApi)
        val suffix = personalGroup.suffix?.ifEmpty { null }?.createComponentWithPlaceholders(player, placeholderApi)
        val name = personalGroup.playerName?.ifEmpty { null }
            ?.createComponentWithPlaceholders(player, placeholderApi) ?: TextComponent.empty()

        if (name.isEmpty) {
            player.playerListName = name
            player.playerListPrefix = prefix
            player.playerListSuffix = suffix
        } else {
            player.playerListName = TextComponent.make {
                if (prefix != null) it.append(prefix)
                it.append(name)
                if (suffix != null) it.append(suffix)
            }
        }
    }

    private fun createPersonalGroup(groups: Collection<TabGroup>): TabGroup {
        var group = TabGroup("", 0)
        for (g in groups.sortedByDescending { it.weight }) {
            if (group.prefix == null && g.prefix != null)
                group = group.copy(prefix = g.prefix)
            if (group.suffix == null && g.suffix != null)
                group = group.copy(suffix = g.suffix)
            if (group.playerName == null && g.playerName != null)
                group = group.copy(playerName = g.playerName)
        }
        return group
    }
}