package com.agoda.boots

import com.agoda.boots.Key.Companion.single

/**
 * This class is used to distinct [bootables][Bootable] in the boot system.
 * Primary goal of a separate class like that is to avoid the need of having
 * direct access to [bootable][Bootable] instances, instead allowing it to be provided/injected
 * by any other means while operating with [keys][Key], which are lightweight and easy to declare.
 */
sealed class Key {

    /**
     * Property that is `true` when all of this key's matching [bootables][Bootable]
     * are in the [idle][Status.Idle] state.
     */
    val isIdle: Boolean
        get() = Boots.report(this).status is Status.Idle

    /**
     * Property that is `true` when any of this key's matching [bootables][Bootable]
     * are in the [booting][Status.Booting] state.
     */
    val isBooting: Boolean
        get() = Boots.report(this).status is Status.Booting

    /**
     * Property that is `true` when all of this key's matching [bootables][Bootable]
     * are in the [booted][Status.Booted] state.
     */
    val isBooted: Boolean
        get() = Boots.report(this).status is Status.Booted

    /**
     * Property that is `true` when any of this key's matching [bootables][Bootable]
     * are in the [failed][Status.Failed] state.
     */
    val isFailed: Boolean
        get() = Boots.report(this).status is Status.Failed

    /**
     * Property that contains current general [status][Status] of all of this
     * key's matching [bootables][Bootable].
     */
    val status: Status
        get() = Boots.report(this).status

    /**
     * Single key. Marks specific [bootable][Bootable] in the system.
     * @param id unique identifier of a key
     */
    class Single(val id: String) : Key() {
        override fun hashCode() = id.hashCode()
        override fun equals(other: Any?) = id == (other as? Single)?.id
        override fun toString() = id
    }

    /**
     * Multiple key. Marks a set of [single][Single] keys.
     * @param keys set of [single][Single] keys
     */
    class Multiple(private val keys: Set<Single>) : Key(), Set<Single> {
        override val size = keys.size

        override fun isEmpty() = keys.isEmpty()
        override fun contains(element: Single) = keys.contains(element)
        override fun containsAll(elements: Collection<Single>) = keys.containsAll(elements)
        override fun iterator() = keys.iterator()
        override fun hashCode() = keys.hashCode()
        override fun equals(other: Any?) = keys == (other as? Multiple)?.keys

        override fun toString() = StringBuilder().apply {
            append("{")
            keys.forEachIndexed { index, key ->
                if (index > 0) append(" ")
                append(key)
                if (index < size - 1) append(",")
            }
            append("}")
        }.toString()

        /**
         * Checks if this key is not empty
         * @return true if this key contains one or more [single][Single] key.
         */
        fun isNotEmpty() = keys.isNotEmpty()
    }

    /**
     * Excluding key. Marks all available keys except given set of [single][Single] keys.
     * @param keys set of [single][Single] keys
     */
    class Excluding(private val keys: Set<Single>) : Key(), Set<Single> {
        override val size = keys.size

        override fun isEmpty() = keys.isEmpty()
        override fun contains(element: Single) = keys.contains(element)
        override fun containsAll(elements: Collection<Single>) = keys.containsAll(elements)
        override fun iterator() = keys.iterator()
        override fun hashCode() = keys.hashCode()
        override fun equals(other: Any?) = keys == (other as? Excluding)?.keys

        override fun toString() = StringBuilder().apply {
            append("{")
            keys.forEachIndexed { index, key ->
                if (index > 0) append(" ")
                append(key)
                if (index < size - 1) append(",")
            }
            append("}")
        }.toString()

        /**
         * Checks if this key is not empty
         * @return true if this key contains one or more [single][Single] key.
         */
        fun isNotEmpty() = keys.isNotEmpty()
    }

    /**
     * Critical key. Used to select all [bootables][Bootable] that have their
     * [isCritical][Bootable.isCritical] flag set to `true`
     */
    class Critical : Key() {
        override fun hashCode() = "CRITICAL".hashCode()
        override fun equals(other: Any?) = other is Critical
        override fun toString() = "CRITICAL"
    }

    /**
     * All key. Used to select all available [bootables][Bootable] in the system.
     */
    class All : Key() {
        override fun hashCode() = "ALL".hashCode()
        override fun equals(other: Any?) = other is All
        override fun toString() = "ALL"
    }

    companion object {
        /**
         * Creates an instance of [Single] key.
         * @param id unique identifier of a key
         * @return instance of [Single]
         */
        @JvmStatic
        fun single(id: String = "") = Key.Single(id)

        /**
         * Creates an instance of [Multiple] key.
         * @param keys [single][Single] keys that form current instance
         * @return instance of [Multiple]
         */
        @JvmStatic
        fun multiple(vararg keys: Key.Single = emptyArray()) = Key.Multiple(keys.toSet())

        /**
         * Creates an instance of [Excluding] key.
         * @param keys [single][Single] keys that form current instance
         * @return instance of [Excluding]
         */
        @JvmStatic
        fun excluding(vararg keys: Key.Single = emptyArray()) = Key.Excluding(keys.toSet())

        /**
         * Creates an instance of [Critical] key.
         * @return instance of [Critical]
         */
        @JvmStatic
        fun critical() = Key.Critical()

        /**
         * Creates an instance of [All] key.
         * @return instance of [All]
         */
        @JvmStatic
        fun all() = Key.All()
    }

}
