package com.proximyst.tab.common.pluginmessage

import java.util.*

data class PlaceholderApiResponse(
    val uuid: UUID,
    val placeholders: Map<String, String>
)