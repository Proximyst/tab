package com.proximyst.tab.common.pluginmessage

import java.util.*

data class PlaceholderApiRequest(
    val uuid: UUID,
    val placeholders: List<String>
)