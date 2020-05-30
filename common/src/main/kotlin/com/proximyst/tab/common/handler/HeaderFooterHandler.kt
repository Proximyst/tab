package com.proximyst.tab.common.handler

import com.proximyst.tab.common.ITabPlayer
import com.proximyst.tab.common.config.commonvalues.HeaderFooterConfig
import net.kyori.text.TextComponent
import net.kyori.text.serializer.legacy.LegacyComponentSerializer

class HeaderFooterHandler(private val headerFooterConfig: HeaderFooterConfig) {
    // TODO(Proximyst): Hook into PlaceholderAPI

    fun <P: ITabPlayer<*>> apply(player: P) {
        if (!headerFooterConfig.enabled) return
        fun createComponent(list: List<String>?) =
            list?.ifEmpty { null }
                ?.map { LegacyComponentSerializer.legacy().deserialize(it, '&') }
                ?.let { components ->
                    TextComponent.make { builder ->
                        components.forEachIndexed { idx, component ->
                            builder.append(component)
                            if (idx != components.size - 1)
                                builder.append(TextComponent.newline())
                        }
                    }
                }

        createComponent(headerFooterConfig.header)?.let { player.playerListHeader = it }
        createComponent(headerFooterConfig.footer)?.let { player.playerListFooter = it }
    }
}