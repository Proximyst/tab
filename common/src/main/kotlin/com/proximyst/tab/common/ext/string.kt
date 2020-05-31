package com.proximyst.tab.common.ext

import com.proximyst.tab.common.IPlaceholderApi
import com.proximyst.tab.common.ITabPlayer
import net.kyori.text.serializer.legacy.LegacyComponentSerializer

fun <Player : ITabPlayer<*>, PlaceholderApi : IPlaceholderApi<Player>>
        String.createComponentWithPlaceholders(player: Player, placeholderApi: PlaceholderApi?) =
    LegacyComponentSerializer.legacy()
        .deserialize(placeholderApi?.replacePlaceholders(player, this) ?: this, '&')