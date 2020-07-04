package com.proximyst.tab.platform.factories;

import com.proximyst.tab.platform.IPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * A factory to create new {@link IPlayer} objects.
 *
 * @param <Underlying> The underlying type for the player.
 * @param <Wrapper>    The wrapper type for the player.
 */
public interface IPlayerFactory<Underlying, Wrapper extends IPlayer> {
  /**
   * Create an {@link IPlayer} from the underlying player type.
   *
   * @param underlying The underlying player.
   * @return The wrapped version of the underlying player.
   */
  @NotNull
  Wrapper createPlayer(@NotNull Underlying underlying);
}
