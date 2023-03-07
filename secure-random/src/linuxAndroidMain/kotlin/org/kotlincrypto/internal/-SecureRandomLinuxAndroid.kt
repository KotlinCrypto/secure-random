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

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.Pinned
import kotlinx.cinterop.addressOf
import platform.posix.EINTR
import platform.posix.errno

/**
 * [GetRandom.getrandom] and [URandom.readBytesTo] return
 * values (if positive) indicate the number of bytes that were
 * put into the [ByteArray]. If it is less than the [size],
 * this ensures that the call is repeated until it has been
 * completely filled.
 * */
internal inline fun Pinned<ByteArray>.fillCompletely(
    size: Int,
    block: (ptr: CPointer<ByteVar>, length: Int) -> Int
) {
    var index = 0
    while (index < size) {

        val remainder = size - index
        val result = block.invoke(addressOf(index), remainder)

        if (result < 0) {
            when (val err = errno) {
                EINTR -> continue // Retry
                else -> throw errnoToSecRandomCopyException(err)
            }
        } else {
            index += result
        }
    }
}
