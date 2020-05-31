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
import net.kyori.text.TextComponent
import net.kyori.text.serializer.legacy.LegacyComponentSerializer

class HeaderFooterHandler<Player : ITabPlayer<*>, PlaceholderApi : IPlaceholderApi<Player>>(
    private val configurationProvider: PlatformTranscendingPlugin.ConfigurationProvider,
    private val placeholderApi: PlaceholderApi?
) {
    fun apply(player: Player) {
        val config = configurationProvider.headerFooterConfig
        if (!config.enabled) return
        fun createComponent(list: List<String>?) =
            list?.ifEmpty { null }
                ?.map {
                    LegacyComponentSerializer.legacy()
                        .deserialize(placeholderApi?.replacePlaceholders(player, it) ?: it, '&')
                }
                ?.let { components ->
                    TextComponent.make { builder ->
                        components.forEachIndexed { idx, component ->
                            builder.append(component)
                            if (idx != components.size - 1)
                                builder.append(TextComponent.newline()).resetStyle()
                        }
                    }
                }

        createComponent(config.header)?.let { player.playerListHeader = it }
        createComponent(config.footer)?.let { player.playerListFooter = it }
    }
}