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
package org.kotlincrypto

import kotlinx.cinterop.usePinned
import org.kotlincrypto.internal.commonNextBytesOf
import org.kotlincrypto.internal.ifNotNullOrEmpty
import org.kotlincrypto.internal.SecRandomDelegate

/**
 * A cryptographically strong random number generator (RNG).
 * */
public actual class SecureRandom {

    private val delegate: SecRandomDelegate

    public actual constructor(): this(SecRandomDelegate)
    internal constructor(delegate: SecRandomDelegate) { this.delegate = delegate }

    /**
     * Returns a [ByteArray] of size [count], filled with
     * securely generated random data.
     *
     * @throws [IllegalArgumentException] if [count] is negative.
     * @throws [SecRandomCopyException] if [nextBytesCopyTo] failed.
     * */
    @Throws(IllegalArgumentException::class, SecRandomCopyException::class)
    public actual fun nextBytesOf(count: Int): ByteArray = commonNextBytesOf(count)

    /**
     * Fills a [ByteArray] with securely generated random data.
     * Does nothing if [bytes] is null or empty.
     *
     * @throws [SecRandomCopyException] if procurement of securely random data failed.
     * */
    @Throws(SecRandomCopyException::class)
    public actual fun nextBytesCopyTo(bytes: ByteArray?) {
        bytes.ifNotNullOrEmpty {
            usePinned { pinned ->
                delegate.nextBytesCopyTo(pinned, size)
            }
        }
    }
}
