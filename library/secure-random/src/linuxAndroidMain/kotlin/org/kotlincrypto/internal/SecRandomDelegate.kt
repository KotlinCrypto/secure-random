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
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.kotlincrypto.internal

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.Pinned
import org.kotlincrypto.SecRandomCopyException

@OptIn(ExperimentalForeignApi::class)
internal actual abstract class SecRandomDelegate private actual constructor() {

    @Throws(SecRandomCopyException::class)
    internal actual abstract fun nextBytesCopyTo(bytes: Pinned<ByteArray>, size: Int)

    internal actual companion object: SecRandomDelegate() {

        @Throws(SecRandomCopyException::class)
        actual override fun nextBytesCopyTo(bytes: Pinned<ByteArray>, size: Int) {
            if (GetRandom.instance.isAvailable()) {
                GetRandom.instance.getrandom(bytes, size)
            } else {
                DelegateURandom.nextBytesCopyTo(bytes, size)
            }
        }
    }

    // Alternative fall back if getrandom is unavailable.
    // It is here so that it can be tested via constructor
    // injection into SecureRandom.
    internal object DelegateURandom: SecRandomDelegate() {

        @Throws(SecRandomCopyException::class)
        override fun nextBytesCopyTo(bytes: Pinned<ByteArray>, size: Int) {
            URandom.instance.readBytesTo(bytes, size)
        }
    }
}
