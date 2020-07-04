package com.proximyst.tab.platform.proxy;

import org.jetbrains.annotations.NotNull;

public interface IProxyPacket<Pkt> {
  @NotNull
  Pkt getPacket();
}
