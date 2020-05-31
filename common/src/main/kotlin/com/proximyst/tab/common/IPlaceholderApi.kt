package com.proximyst.tab.common

interface IPlaceholderApi<P: ITabPlayer<*>> {
    fun replacePlaceholders(player: P, text: String): String
}