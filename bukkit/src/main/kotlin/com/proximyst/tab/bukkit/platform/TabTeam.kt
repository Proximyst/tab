package com.proximyst.tab.bukkit.platform

import com.proximyst.tab.bukkit.BukkitPlayer
import org.bukkit.scoreboard.Team

class TabTeam(val team: Team) : Team by team {
    constructor(player: BukkitPlayer) : this(
        player.platformPlayer.scoreboard
            .also {
                // The team may exist from a faulty start.
                it.getTeam("TAB_${player.uniqueId.hashCode()}")?.unregister()
            }
            .registerNewTeam(
                "TAB_${player.uniqueId.hashCode()}"
            )
    )
}