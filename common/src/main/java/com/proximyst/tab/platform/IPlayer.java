package com.proximyst.tab.platform;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A player type on a platform.
 */
public interface IPlayer {
  /**
   * @return Whether the player is currently connected.
   */
  boolean isConnected();

  /**
   * @return Whether the player is currently disconnected.
   */
  default boolean isDisconnected() {
    return !isConnected();
  }

  /**
   * @return The name of the player with no magic, colours, or other changes applied.
   */
  @NotNull
  String getName();

  /**
   * @return The name of the player in the player list. This is simply their name if no custom name has been set.
   */
  @NotNull
  Component getPlayerListName();

  /**
   * Set the name of the player in the player list.
   *
   * @param component The component to change the name to. A value of null resets the name.
   */
  void setPlayerListName(@Nullable Component component);

  /**
   * Check whether the player has a permission node granted.
   *
   * @param permissionNode The permission node to check.
   * @return Whether the player has the permission granted or otherwise is assumed granted.
   */
  boolean hasPermission(@NotNull String permissionNode);

  /**
   * Clean up the data for this player and invalidate the object.
   */
  default void cleanup() {
  }
}
