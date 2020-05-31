package com.proximyst.tab.common.handler

import com.proximyst.tab.common.IPlaceholderApi
import com.proximyst.tab.common.ITabPlayer
import com.proximyst.tab.common.ext.createComponentWithPlaceholders
import com.proximyst.tab.common.model.TabGroup
import net.kyori.text.TextComponent

class PlayerNameHandler<Player : ITabPlayer<*>, PlaceholderApi : IPlaceholderApi<Player>>(
    private val groups: List<TabGroup>,
    private val placeholderApi: PlaceholderApi?
) {
    fun apply(player: Player) {
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