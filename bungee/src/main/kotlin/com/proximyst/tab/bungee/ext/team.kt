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
package com.proximyst.tab.bungee.ext

import net.md_5.bungee.api.score.Team
import net.md_5.bungee.protocol.packet.Team as TeamPacket

/**
 * Create a new [TeamPacket] from this [Team] which creates a new team in the
 * client.
 */
fun Team.toCreationPacket(): TeamPacket {
    // For some reason, the lombok abusers did not find a builder to be sensible.
    val packet = TeamPacket()
    packet.mode = 0 // 0 = create new team
    packet.name = name
    // Display name has to be the same as the actual name if one is not provided.
    packet.displayName = displayName ?: "{\"text\":\"${name.replace("\"", "\\\"")}\"}"
    packet.collisionRule = collisionRule ?: "always"
    packet.color = color
    packet.friendlyFire = friendlyFire
    packet.nameTagVisibility = nameTagVisibility ?: "always"
    packet.prefix = prefix ?: "{\"text\":\"\"}" // These have to be empty to not cause NPEs in BungeeCord
    packet.suffix = suffix ?: "{\"text\":\"\"}" // These have to be empty to not cause NPEs in BungeeCord
    packet.players = players.toTypedArray()
    return packet
}