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
@file:Suppress("SpellCheckingInspection", "UnnecessaryOptInAnnotation")

package org.kotlincrypto.internal

import kotlinx.cinterop.*
import org.kotlincrypto.SecRandomCopyException
import platform.posix.*
import kotlin.concurrent.AtomicInt

/**
 * Helper for:
 *  - Ensuring /dev/random has been seeded
 *  - Obtaining bytes from /dev/urandom
 *
 * Polling of /dev/random via [ensureSeeded] is always called
 * to ensure that no data is read from /dev/urandom until there
 * is adequate randomness generated. [ensureSeeded] only ever
 * polls /dev/random once, afterwhich it does nothing.
 *
 * @see [SecRandomSynchronized]
 * @see [readBytesTo]
 * */
@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
internal class URandom private constructor(): SecRandomSynchronized() {

    internal companion object {
        private const val INFINITE_TIMEOUT = -1

        internal val instance = URandom()
    }

    private val lock = Lock()

    /**
     * Reads bytes from /dev/urandom of specified [buflen] into [buf].
     *
     * @see [withReadOnlyFD]
     * @see [fillCompletely]
     * */
    @Throws(SecRandomCopyException::class)
    internal fun readBytesTo(buf: Pinned<ByteArray>, buflen: Int) {
        ensureSeeded()

        lock.withLock {
            withReadOnlyFD("/dev/urandom") { fd ->
                buf.fillCompletely(buflen) { ptr, length ->
                    read(fd, ptr, length.toULong().convert()).convert()
                }
            }
        }
    }

    /**
     * Polls /dev/random once and only once to ensure /dev/urandom
     * is ready to read from. Any sucessive calls to [ensureSeeded]
     * will block until [synchronizedRemember]'s lambda is executed.
     * */
    private fun ensureSeeded() {
        synchronizedRemember { _, _ ->

            // Will only ever be invoked once
            withReadOnlyFD("/dev/random") { fd ->
                memScoped {
                    val pollFd = nativeHeap.alloc<pollfd>()
                    pollFd.apply {
                        this.fd = fd
                        events = POLLIN.convert()
                        revents = 0
                    }

                    while (true) {
                        val result = poll(pollFd.ptr, 1u, INFINITE_TIMEOUT)
                        if (result >= 0) {
                            break
                        }
                        when (val err = errno) {
                            EINTR,
                            EAGAIN -> continue
                            else -> throw errnoToSecRandomCopyException(err)
                        }
                    }
                }
            }

            true
        }
    }

    /**
     * Opens the file descriptor for [path] and then closes
     * it once [block] completes.
     * */
    private fun withReadOnlyFD(path: String, block: (fd: Int) -> Unit) {
        val fd = open(path, O_RDONLY, null)
        if (fd == -1) throw errnoToSecRandomCopyException(errno)

        try {
            block.invoke(fd)
        } finally {
            close(fd)
        }
    }

    private inner class Lock {

        // 0: Unlocked
        // *: Locked
        private val lock = AtomicInt(0)

        fun <T> withLock(block: () -> T): T {
            val lockId = Any().hashCode()

            try {
                while (true) {
                    if (lock.compareAndSet(0, lockId)) {
                        break
                    }
                }

                return block.invoke()
            } finally {
                lock.compareAndSet(lockId, 0)
            }
        }
    }
}
