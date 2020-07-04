package com.proximyst.tab.platform.factories;

import com.proximyst.tab.platform.ITeam;
import com.proximyst.tab.platform.proxy.IProxyPlatform;
import org.jetbrains.annotations.NotNull;

/**
 * A factory to create new {@link ITeam} objects.
 *
 * @param <Underlying> The underlying type for the team.
 * @param <Wrapper>    The wrapper type for the team.
 */
public interface ITeamFactory<Underlying, Wrapped extends ITeam> {
  /**
   * Create an {@link ITeam} from the underlying team type.
   * <p>
   * On {@link IProxyPlatform}s, this might simply return the same instance.
   * </p>
   *
   * @param underlying The underlying team.
   * @return The wrapped version of the underlying team.
   */
  @NotNull
  Wrapped createTeam(@NotNull Underlying underlying);
}
