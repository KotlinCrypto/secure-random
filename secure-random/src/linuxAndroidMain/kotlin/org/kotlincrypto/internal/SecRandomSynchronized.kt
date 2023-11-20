/*
 * Copyright (c) 2023 Matthew Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package org.kotlincrypto.internal

import kotlinx.cinterop.*
import kotlin.concurrent.AtomicInt

@OptIn(ExperimentalForeignApi::class)
internal abstract class SecRandomSynchronized {

    private companion object {
        private const val TRUE = 1
        private const val FALSE = 0
        private const val UNKNOWN = -1
    }

    private val result = AtomicInt(UNKNOWN)

    /**
     * Synchronized execution of [action] such that it is only ever
     * invoked once, and it's response stored in [result] which
     * will be returned henceforth.
     * */
    protected fun synchronizedRemember(
        action: (buf: CPointer<ByteVar>, size: Int) -> Boolean
    ): Boolean {
        when (result.value) {
            TRUE -> return true
            FALSE -> return false
            else -> {
                val buf = ByteArray(1)

                // Use the hashCode as the id for obtaining
                // the lock for this invocation of
                // synchronizedRemember
                val lockId = buf.hashCode()

                try {
                    return executeActionMaybe(buf, lockId, action)
                } finally {
                    // Ensure the lock is reset back to UNKNOWN
                    // if it was not set to t/f so that it can
                    // be obtained again
                    result.compareAndSet(lockId, UNKNOWN)
                }
            }
        }
    }

    private fun executeActionMaybe(
        buf: ByteArray,
        lockId: Int,
        action: (buf: CPointer<ByteVar>, size: Int) -> Boolean
    ): Boolean {

        // Acquire the lock or return another invocation
        // instance's result.
        while (true) {
            when (result.value) {
                TRUE -> return true
                FALSE -> return false
                else -> {
                    if (result.compareAndSet(UNKNOWN, lockId)) {
                        break
                    }
                }
            }
        }

        // Lock obtained. Execute and store result.
        val result: Boolean = buf.usePinned { pinned ->
            action.invoke(pinned.addressOf(0), buf.size)
        }

        this.result.compareAndSet(lockId, if (result) TRUE else FALSE)

        return result
    }
}
