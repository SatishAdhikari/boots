package com.agoda.boots.executor

import com.agoda.boots.Executor
import com.agoda.boots.Executor.Companion.DEFAULT_CAPACITY
import kotlinx.coroutines.*

/**
 * Coroutine implementation of [Executor].
 *
 * Can switch to dispatcher to launch [concurrent][com.agoda.boots.Bootable.isConcurrent] bootables.
 */
open class CoroutineExecutor @JvmOverloads constructor(
    private val coroutineScope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
    override val capacity: Int = DEFAULT_CAPACITY
) : Executor {

    override val isMainThreadSupported = false

    override fun execute(isConcurrent: Boolean, executable: () -> Unit) {
        coroutineScope.launch {
            if (isConcurrent) {
                withContext(dispatcher) {
                    executable.invoke()
                }
            } else {
                executable.invoke()
            }
        }
    }
}
