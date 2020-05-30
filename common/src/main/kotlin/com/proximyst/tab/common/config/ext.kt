package com.proximyst.tab.common.config

import com.google.gson.reflect.TypeToken
import com.proximyst.tab.common.ext.typeToken
import kotlin.reflect.KClass

/**
 * Delegate the config value requested with type [R] to the name or the property name with an optional default.
 *
 * @return The config value delegate.
 */
@Suppress("DEPRECATION")
inline fun <reified R : Any> config(
    name: String? = null,
    noinline default: () -> R? = { null }
) = __config_internal_getter(name, typeToken<R>(), R::class, default)

@Suppress("UnstableApiUsage", "DeprecatedCallableAddReplaceWith")
@Deprecated("This is an internal API.")
private fun <R : Any> config(
    name: String?,
    typeToken: TypeToken<out R>,
    type: KClass<out R>,
    default: () -> R?
) = TomlDelegatedConfigurationValue(name, typeToken, type, default)

@Suppress("FunctionName", "DEPRECATION", "DeprecatedCallableAddReplaceWith", "UnstableApiUsage")
@PublishedApi
@Deprecated("This is an internal API.")
internal fun <R : Any> __config_internal_getter(
    name: String?,
    typeToken: TypeToken<out R>,
    type: KClass<out R>,
    default: () -> R?
) = config(name, typeToken, type, default)