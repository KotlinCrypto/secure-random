/*
 * Copyright (c) 2023 Matthew Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package org.kotlincrypto

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
                if (isNode) {
                    _require("crypto").randomFillSync(this)
                } else {
                    global.crypto.getRandomValues(this)
                }

                Unit
            } catch (t: Throwable) {
                throw SecRandomCopyException("Failed to obtain bytes", t)
            }
        }
    }

    private companion object {
        private val isNode: Boolean by lazy {
            val runtime: String? = try {
                // May not be available, but should be preferred
                // method of determining runtime environment.
                js("(globalThis.process.release.name)") as String
            } catch (_: Throwable) {
                null
            }

            when (runtime) {
                null -> {
                    js("(typeof global !== 'undefined' && ({}).toString.call(global) == '[object global]')") as Boolean
                }
                "node" -> true
                else -> false
            }
        }

        private val global: dynamic by lazy {
            js("((typeof global !== 'undefined') ? global : self)")
        }

        @Suppress("FunctionName", "UNUSED_PARAMETER")
        private fun _require(name: String): dynamic = js("require(name)")
    }
}
