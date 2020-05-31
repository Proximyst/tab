package com.proximyst.tab.common.handler

import com.proximyst.tab.common.IPlaceholderApi
import com.proximyst.tab.common.ITabPlayer
import com.proximyst.tab.common.config.commonvalues.HeaderFooterConfig
import net.kyori.text.TextComponent
import net.kyori.text.serializer.legacy.LegacyComponentSerializer

class HeaderFooterHandler<Player : ITabPlayer<*>, PlaceholderApi : IPlaceholderApi<Player>>(
    private val headerFooterConfig: HeaderFooterConfig,
    private val placeholderApi: PlaceholderApi?
) {
    fun apply(player: Player) {
        if (!headerFooterConfig.enabled) return
        fun createComponent(list: List<String>?) =
            list?.ifEmpty { null }
                ?.map {
                    LegacyComponentSerializer.legacy()
                        .deserialize(placeholderApi?.replacePlaceholders(player, it) ?: it, '&')
                }
                ?.let { components ->
                    TextComponent.make { builder ->
                        components.forEachIndexed { idx, component ->
                            builder.append(component)
                            if (idx != components.size - 1)
                                builder.append(TextComponent.newline()).resetStyle()
                        }
                    }
                }

        createComponent(headerFooterConfig.header)?.let { player.playerListHeader = it }
        createComponent(headerFooterConfig.footer)?.let { player.playerListFooter = it }
    }
}