package com.proximyst.tab.common.handler

import com.proximyst.tab.common.IPlaceholderApi
import com.proximyst.tab.common.ITabPlayer
import com.proximyst.tab.common.PlatformTranscendingPlugin
import net.kyori.text.TextComponent
import net.kyori.text.serializer.legacy.LegacyComponentSerializer

class HeaderFooterHandler<Player : ITabPlayer<*>, PlaceholderApi : IPlaceholderApi<Player>>(
    private val configurationProvider: PlatformTranscendingPlugin.ConfigurationProvider,
    private val placeholderApi: PlaceholderApi?
) {
    fun apply(player: Player) {
        val config = configurationProvider.headerFooterConfig
        if (!config.enabled) return
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

        createComponent(config.header)?.let { player.playerListHeader = it }
        createComponent(config.footer)?.let { player.playerListFooter = it }
    }
}