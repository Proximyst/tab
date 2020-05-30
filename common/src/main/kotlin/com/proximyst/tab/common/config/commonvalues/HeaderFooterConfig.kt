package com.proximyst.tab.common.config.commonvalues

data class HeaderFooterConfig(
    val enabled: Boolean,
    val header: List<String>?,
    val footer: List<String>?
)