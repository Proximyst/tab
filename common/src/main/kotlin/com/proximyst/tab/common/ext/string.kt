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
package com.proximyst.tab.common.ext

import com.proximyst.tab.common.IPlaceholderApi
import com.proximyst.tab.common.ITabPlayer
import net.kyori.text.Component
import net.kyori.text.TextComponent
import net.kyori.text.serializer.legacy.LegacyComponentSerializer

/**
 * Create a KyoriPowered/text [Component] out of a [String] and its placeholders
 * with the given [Player] and [PlaceholderApi] instances.
 *
 * This also handles colour codes with the `&` character.
 */
fun <Player : ITabPlayer<*>, PlaceholderApi : IPlaceholderApi<Player>>
        String.createComponentWithPlaceholders(player: Player, placeholderApi: PlaceholderApi?): TextComponent =
    LegacyComponentSerializer.legacy()
        .deserialize(placeholderApi?.replacePlaceholders(player, this) ?: this, '&')