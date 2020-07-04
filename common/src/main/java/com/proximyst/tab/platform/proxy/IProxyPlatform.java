package com.proximyst.tab.platform.proxy;

import com.proximyst.tab.platform.IPlatform;
import com.proximyst.tab.platform.proxy.factories.IProxyPacketFactory;
import org.jetbrains.annotations.NotNull;

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

  /**
   * @return This platform's {@link IProxyPacketFactory}.
   */
  @NotNull
  IProxyPacketFactory<?, Player> getProxyPacketFactory();
}
