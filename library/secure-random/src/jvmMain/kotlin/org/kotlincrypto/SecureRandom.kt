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
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING", "ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT")

package org.kotlincrypto

import org.kotlincrypto.internal.commonNextBytesOf
import org.kotlincrypto.internal.ifNotNullOrEmpty

/**
 * A cryptographically strong random number generator (RNG).
 *
 * @see [java.security.SecureRandom]
 * */
@Deprecated("Deprecated in favor of CryptoRand. See https://github.com/KotlinCrypto/crypto-rand")
public actual class SecureRandom public actual constructor(): java.security.SecureRandom() {

    /**
     * Returns a [ByteArray] of size [count], filled with
     * securely generated random data.
     *
     * @throws [IllegalArgumentException] if [count] is negative.
     * */
    @Throws(IllegalArgumentException::class)
    public actual fun nextBytesOf(count: Int): ByteArray = commonNextBytesOf(count)

    /**
     * Fills a [ByteArray] with securely generated random data.
     * Does nothing if [bytes] is null or empty.
     *
     * @see [java.security.SecureRandom.nextBytes]
     * */
    public actual fun nextBytesCopyTo(bytes: ByteArray?) {
        bytes.ifNotNullOrEmpty { super.nextBytes(this) }
    }
}
