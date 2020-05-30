package com.proximyst.tab.common.config

import com.google.gson.reflect.TypeToken
import com.moandjiezana.toml.Toml
import com.proximyst.tab.common.ITabPlatform
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * A delegated configuration value to a [TomlConfiguration] of an [ITabPlatform] of type [type].
 */
@Suppress("FINITE_BOUNDS_VIOLATION_IN_JAVA", "UnstableApiUsage", "UNCHECKED_CAST")
class TomlDelegatedConfigurationValue(
    private val name: String?,
    private val type: TypeToken<*>,
    private val clazz: KClass<*>,
    private val default: () -> Any?
) {
    private var properName: String? = null
    private var lazyValue = object : Lazy<Any?> {
        var initialized = false
        override var value: Any? = null
        override fun isInitialized() = initialized
    }

    private fun setProperName(property: KProperty<*>): String {
        properName?.let { return it }
        if (name != null) {
            properName = name
            return name
        }

        val new = buildString(property.name.length) {
            for (char in property.name) {
                if (char.isUpperCase()) append('-')
                append(char.toLowerCase())
            }
        }
        properName = new
        return new
    }

    operator fun <T> getValue(thisRef: ITabPlatform<*>, property: KProperty<*>): T {
        if (lazyValue.isInitialized()) {
            return lazyValue.value as T
        }
        lazyValue.initialized = true

        val name = setProperName(property)

        if (name !in thisRef.tomlConfig.toml) {
            val defaulted = default()
            if (defaulted != null && !clazz.isInstance(defaulted))
                throw ClassCastException("defaulted value (${defaulted.javaClass.simpleName}) is not of type $type")
            val value = default()
            thisRef.tomlConfig.toml[name] = value
            return value as T
        }

        var value = runCatching {
            when (clazz) {
                String::class -> thisRef.tomlConfig.toml.getString(name)
                Int::class -> thisRef.tomlConfig.toml.getInt(name)
                Long::class -> thisRef.tomlConfig.toml.getLong(name)
                Double::class -> thisRef.tomlConfig.toml.getDouble(name)
                Float::class -> thisRef.tomlConfig.toml.getFloat(name)
                Date::class -> thisRef.tomlConfig.toml.getDate(name)
                Boolean::class -> thisRef.tomlConfig.toml.getBoolean(name)
                Toml::class -> thisRef.tomlConfig.toml.getTable(name)?.toml
                TomlKt::class -> thisRef.tomlConfig.toml.getTable(name)
                else -> thisRef.tomlConfig.toml.getTable(name)?.to(type)
            }
        }.getOrNull()

        if (value == null) {
            value = default()
            if (value != null) {
                thisRef.tomlConfig.toml[name] = value
            }
        }
        lazyValue.value = value

        return value as T
    }

    operator fun <T> setValue(thisRef: ITabPlatform<*>, property: KProperty<*>, value: T) {
        val name = setProperName(property)

        thisRef.tomlConfig.toml[name] = value
        lazyValue.value = value
    }
}