package com.proximyst.tab.platform.proxy;

import com.proximyst.tab.platform.IPlatform;

/**
 * A {@link IPlatform} that is a proxy server.
 *
 * @param <Player> The type of player for this platform.
 */
public interface IProxyPlatform<Player extends IProxyPlayer> extends IPlatform<Player> {
  /**
   * {@inheritDoc}
   */
  @Override
  default boolean isProxy() {
    return true;
  }
}
