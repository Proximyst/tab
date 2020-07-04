package com.proximyst.tab.platform;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/**
 * A team for use with the player list.
 */
public interface ITeam {
  /**
   * @return The name of the team as send to players.
   */
  @NotNull
  String getName();

  /**
   * @return The players this team contains.
   */
  @NotNull
  Collection<? extends IPlayer> getPlayers();
}
