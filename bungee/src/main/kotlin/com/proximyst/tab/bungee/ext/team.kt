package com.proximyst.tab.bungee.ext

import net.md_5.bungee.api.score.Team
import net.md_5.bungee.protocol.packet.Team as TeamPacket

fun Team.toCreationPacket(): TeamPacket {
    val packet = TeamPacket()
    packet.mode = 0
    packet.name = name
    packet.displayName = displayName ?: "{\"text\":\"${name.replace("\"", "\\\"")}\"}"
    packet.collisionRule = collisionRule ?: "always"
    packet.color = color
    packet.friendlyFire = friendlyFire
    packet.nameTagVisibility = nameTagVisibility ?: "always"
    packet.prefix = prefix ?: "{\"text\":\"\"}"
    packet.suffix = suffix ?: "{\"text\":\"\"}"
    packet.players = players.toTypedArray()
    return packet
}