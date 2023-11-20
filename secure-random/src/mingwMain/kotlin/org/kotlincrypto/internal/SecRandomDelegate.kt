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

import kotlinx.cinterop.*
import org.kotlincrypto.SecRandomCopyException
import platform.windows.BCRYPT_USE_SYSTEM_PREFERRED_RNG
import platform.windows.BCryptGenRandom
import platform.windows.STATUS_INVALID_HANDLE
import platform.windows.STATUS_INVALID_PARAMETER

/**
 * https://learn.microsoft.com/en-us/windows/win32/api/bcrypt/nf-bcrypt-bcryptgenrandom
 * */
@OptIn(ExperimentalForeignApi::class)
internal actual abstract class SecRandomDelegate private actual constructor() {

    @Throws(SecRandomCopyException::class)
    internal actual abstract fun nextBytesCopyTo(bytes: Pinned<ByteArray>, size: Int)

    internal actual companion object: SecRandomDelegate() {

        @Throws(SecRandomCopyException::class)
        actual override fun nextBytesCopyTo(bytes: Pinned<ByteArray>, size: Int) {
            val status = BCryptGenRandom(
                null,
                bytes.addressOf(0).reinterpret(),
                size.toULong().convert(),
                BCRYPT_USE_SYSTEM_PREFERRED_RNG.convert(),
            ).toUInt()

            when (status) {
                STATUS_INVALID_HANDLE,
                STATUS_INVALID_PARAMETER -> throw SecRandomCopyException(errorToString(status))
            }
        }
    }
}
