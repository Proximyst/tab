package com.proximyst.tab.platform.proxy.factories;

import com.proximyst.tab.platform.ITeam;
import com.proximyst.tab.platform.proxy.IProxyPacket;
import com.proximyst.tab.platform.proxy.IProxyPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * A factory to create new {@link IProxyPacket} objects.
 *
 * @param <Team>   The type for the teams on the proxy.
 * @param <Player> The type of proxied players for the proxy.
 */
public interface IProxyPacketFactory<
    Team extends ITeam,
    Player extends IProxyPlayer
    > {
  /**
   * Create a packet which defines/creates a new team for the receiving player.
   *
   * @param team The team to create a packet for.
   * @return A team creation packet.
   */
  @NotNull
  IProxyPacket<?> createTeamCreationPacket(@NotNull Team team);

  /**
   * Create a packet which updates a team for the receiving player.
   *
   * @param team     The team to create a packet for.
   * @param receiver The player who's supposed to receive this packet.
   * @return A team update packet.
   */
  @NotNull
  IProxyPacket<?> createTeamUpdatePacket(@NotNull Team team, @NotNull Player receiver);

  /**
   * Create a packet which removes a team for the receiving player.
   *
   * @param team The team to create a packet for.
   * @return A team removal packet.
   */
  @NotNull
  IProxyPacket<?> createTeamRemovalPacket(@NotNull Team team);
}
