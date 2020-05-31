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
import net.kyori.text.TextComponent

/**
 * Handler for header & footer sending and updating.
 *
 * This handles both sending of header & footer and placeholder replacements.
 */
class HeaderFooterHandler<Player : ITabPlayer<*>, PlaceholderApi : IPlaceholderApi<Player>>(
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
     * Apply the header & footer to the given [Player] if it has any to be
     * applied.
     */
    fun apply(player: Player) {
        // Get the current configuration as a snapshot.
        // This ensures nothing becomes null under our noses.
        val config = configurationProvider.headerFooterConfig
        if (!config.enabled) return

        fun createComponent(list: List<String>?) =
            list?.ifEmpty { null }
                ?.map { it.createComponentWithPlaceholders(player, placeholderApi) }
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

        createComponent(config.header)?.let { player.playerListHeader = it }
        createComponent(config.footer)?.let { player.playerListFooter = it }
    }
}