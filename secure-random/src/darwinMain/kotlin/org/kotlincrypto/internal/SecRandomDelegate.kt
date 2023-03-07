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

import kotlinx.cinterop.Pinned
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import org.kotlincrypto.SecRandomCopyException

/**
 * https://developer.apple.com/documentation/security/1399291-secrandomcopybytes
 * */
internal actual abstract class SecRandomDelegate private actual constructor() {

    @Throws(SecRandomCopyException::class)
    internal actual abstract fun nextBytesCopyTo(bytes: Pinned<ByteArray>, size: Int)

    internal actual companion object: SecRandomDelegate() {

        @OptIn(UnsafeNumber::class)
        @Throws(SecRandomCopyException::class)
        actual override fun nextBytesCopyTo(bytes: Pinned<ByteArray>, size: Int) {
            // kSecRandomDefault is synonymous to NULL
            val errno: Int = SecRandomCopyBytes(kSecRandomDefault, size.toUInt().convert(), bytes.addressOf(0))
            if (errno != 0) {
                throw errnoToSecRandomCopyException(errno)
            }
        }
    }
}
