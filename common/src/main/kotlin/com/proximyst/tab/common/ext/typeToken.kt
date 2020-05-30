package com.proximyst.tab.common.ext

import com.google.gson.reflect.TypeToken

@Suppress("UnstableApiUsage")
inline fun <reified T : Any> typeToken(): TypeToken<T> = object : TypeToken<T>() {}