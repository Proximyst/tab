package com.proximyst.tab.platform.proxy;

import com.proximyst.tab.platform.IPlayer;

/**
 * A {@link IPlayer} specific to proxy platforms.
 */
public interface IProxyPlayer extends IPlayer {
  /**
   * Send a packet down the pipeline of the player.
   *
   * @param packet The packet to send, wrapped.
   * @param <Pkt>  The type of the packet to send.
   */
  <Pkt> void sendPacket(IProxyPacket<Pkt> packet);
}
