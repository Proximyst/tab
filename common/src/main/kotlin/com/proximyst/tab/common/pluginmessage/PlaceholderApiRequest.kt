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
package com.proximyst.tab.common.pluginmessage

import java.util.*

/**
 * A request for information on placeholders for a specific player.
 *
 * No response is actually expected, but a new request will be issued every time
 * a placeholder is used.
 */
data class PlaceholderApiRequest(
    /**
     * The [UUID] of the player to whom the placeholders apply.
     */
    val uuid: UUID,

    /**
     * The placeholders whose values are wanted an updated version of.
     */
    val placeholders: List<String>
)