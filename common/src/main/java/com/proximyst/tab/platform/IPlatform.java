package com.proximyst.tab.platform;

import com.proximyst.tab.platform.factories.IPlayerFactory;
import java.util.Collection;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A platform for the plugin to run on.
 *
 * @param <Player> The type of player for this platform.
 */
public interface IPlatform<Player extends IPlayer> {
  /**
   * @return Whether this platform is a proxy.
   */
  default boolean isProxy() {
    return false;
  }

  /**
   * @return All currently connected players.
   */
  @NotNull
  Collection<? extends Player> getConnectedPlayers();

  /**
   * Get a connected player by their UUID.
   *
   * @param uuid The UUID to get.
   * @return The player found or null if none.
   */
  @Nullable
  Player getPlayer(@NotNull UUID uuid);

  /**
   * Get a connected player by their name.
   *
   * @param name The name to get.
   * @return The player found or null if none.
   */
  @Nullable
  Player getPlayer(@NotNull String name);

  /**
   * @return This platform's {@link IPlayerFactory}.
   */
  @NotNull
  IPlayerFactory<?, Player> getPlayerFactory();
}
