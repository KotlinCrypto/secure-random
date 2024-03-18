/*
 * Copyright (c) 2024 Matthew Nelson
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
@file:Suppress("ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.kotlincrypto

import org.khronos.webgl.Int8Array
import org.khronos.webgl.get
import org.khronos.webgl.set
import org.kotlincrypto.internal.commonNextBytesOf
import org.kotlincrypto.internal.ifNotNullOrEmpty

/**
 * A cryptographically strong random number generator (RNG).
 * */
public actual class SecureRandom public actual constructor() {

    /**
     * Returns a [ByteArray] of size [count], filled with
     * securely generated random data.
     *
     * @throws [IllegalArgumentException] if [count] is negative.
     * @throws [SecRandomCopyException] if [nextBytesCopyTo] failed.
     * */
    public actual fun nextBytesOf(count: Int): ByteArray = commonNextBytesOf(count)

    /**
     * Fills a [ByteArray] with securely generated random data.
     * Does nothing if [bytes] is null or empty.
     *
     * Node: https://nodejs.org/api/crypto.html#cryptorandomfillsyncbuffer-offset-size
     * Browser: https://developer.mozilla.org/docs/Web/API/Crypto/getRandomValues
     *
     * @throws [SecRandomCopyException] if procurement of securely random data failed.
     * */
    public actual fun nextBytesCopyTo(bytes: ByteArray?) {
        bytes.ifNotNullOrEmpty {
            try {
                val array = Int8Array(size)

                if (isNode) {
                    crypto.randomFillSync(array)
                } else {
                    var offset = 0
                    while (offset < size) {
                        val len = if (size > 65536) 65536 else size
                        crypto.getRandomValues(array.subarray(offset, offset + len))
                        offset += len
                    }
                }

                for (i in indices) {
                    this[i] = array[i]
                    array[i] = 0
                }
            } catch (t: Throwable) {
                throw SecRandomCopyException("Failed to obtain bytes", t)
            }
        }
    }

    private companion object {
        private val isNode: Boolean by lazy { isNodeJs() }
        private val crypto: Crypto by lazy { if (isNode) cryptoNode() else cryptoBrowser() }
    }
}

private fun cryptoNode(): Crypto = js("eval('require')('crypto')")
private fun cryptoBrowser(): Crypto = js("(window ? (window.crypto ? window.crypto : window.msCrypto) : self.crypto)")

private fun isNodeJs(): Boolean = js(
    """
(typeof process !== 'undefined' 
    && process.versions != null 
    && process.versions.node != null) ||
(typeof window !== 'undefined' 
    && typeof window.process !== 'undefined' 
    && window.process.versions != null 
    && window.process.versions.node != null)
"""
)

private external class Crypto: JsAny {
    // Browser
    fun getRandomValues(array: Int8Array)
    // Node.js
    fun randomFillSync(array: Int8Array)
}
