package com.proximyst.tab.common.config

import com.google.gson.reflect.TypeToken
import com.moandjiezana.toml.Toml
import com.proximyst.tab.common.ext.typeToken
import java.io.File
import java.io.InputStream
import java.io.Reader
import java.util.*

/**
 * A [Toml] wrapper for use in Kotlin.
 */
data class TomlKt(internal val toml: Toml) {
    val isEmpty: Boolean
        get() = toml.isEmpty

    operator fun contains(key: String) = toml.contains(key)
    fun containsPrimitive(key: String) = toml.containsPrimitive(key)
    fun containsTable(key: String) = toml.containsTable(key)
    fun containsTableArray(key: String) = toml.containsTableArray(key)

    fun getBoolean(key: String): Boolean? = toml.getBoolean(key)
    fun getBoolean(key: String, default: Boolean): Boolean = toml.getBoolean(key, default)

    fun getDate(key: String): Date? = toml.getDate(key)
    fun getDate(key: String, default: Date): Date = toml.getDate(key, default)

    fun getFloat(key: String) = getDouble(key)?.toFloat()
    fun getFloat(key: String, default: Float) = getDouble(key, default.toDouble()).toFloat()

    fun getDouble(key: String): Double? = toml.getDouble(key)
    fun getDouble(key: String, default: Double): Double = toml.getDouble(key, default)

    fun <T> getList(key: String): List<T>? = toml.getList(key)
    fun <T> getList(key: String, default: List<T>): List<T> = toml.getList(key, default)

    fun <T> getSet(key: String): Set<T>? = getList<T>(key)?.toSet()
    fun <T> getSet(key: String, default: Set<T>): Set<T> = getList<T>(key)?.toSet() ?: default

    fun getString(key: String): String? = toml.getString(key)
    fun getString(key: String, default: String): String = toml.getString(key, default)

    fun getInt(key: String): Int? = toml.getLong(key)?.toInt()
    fun getInt(key: String, default: Int): Int = toml.getLong(key, default.toLong()).toInt()

    fun getLong(key: String): Long? = toml.getLong(key)
    fun getLong(key: String, default: Long): Long = toml.getLong(key, default)

    fun getTable(key: String): TomlKt? = toml.getTable(key)?.let(::TomlKt)
    fun getTable(key: String, default: TomlKt): TomlKt = getTable(key) ?: default
    fun getTable(key: String, default: Toml): TomlKt = getTable(key) ?: default.let(::TomlKt)
    fun getTable(key: String, default: () -> TomlKt): TomlKt = getTable(key) ?: default()

    fun getTables(key: String): List<TomlKt> = toml.getTables(key).mapTo(mutableListOf(), ::TomlKt)

    fun toMap(): Map<String, Any?> = toml.toMap()

    operator fun set(key: String, value: Any?) {
        toml[key] = value
    }

    fun <T : Any> to(type: Class<T>): T = toml.to(type)
    fun <T : Any> to(type: TypeToken<T>): T =
        Toml.DEFAULT_GSON.fromJson(Toml.DEFAULT_GSON.toJsonTree(toml.toMap()), type.type)

    inline fun <reified T : Any> to() = to(typeToken<T>())

    fun read(file: File): TomlKt {
        toml.read(file)
        return this
    }

    fun read(inputStream: InputStream): TomlKt {
        toml.read(inputStream)
        return this
    }

    fun read(reader: Reader): TomlKt {
        toml.read(reader)
        return this
    }

    fun read(toml: String): TomlKt {
        this.toml.read(toml)
        return this
    }

    fun read(toml: Toml): TomlKt {
        this.toml.read(toml)
        return this
    }

    fun read(toml: TomlKt): TomlKt {
        this.toml.read(toml.toml)
        return this
    }

    @Suppress("SuspiciousEqualsCombination") // The Toml class does not override equals
    override fun equals(other: Any?): Boolean = when (other) {
        is Toml -> other === toml || other == toml
        is TomlKt -> other === this || other.toml == toml
        else -> false
    }

    override fun hashCode() = toml.hashCode()
    override fun toString() = toml.toMap().toString()
}