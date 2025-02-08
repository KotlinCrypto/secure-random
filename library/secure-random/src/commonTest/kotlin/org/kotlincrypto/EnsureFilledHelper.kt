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
@file:Suppress("DEPRECATION")

package org.kotlincrypto

import kotlin.test.assertTrue

/**
 * Test helper to extend for some platforms which have fallback
 * implementations if something is not available.
 * */
abstract class EnsureFilledHelper {

    protected abstract val sRandom: SecureRandom

    // https://github.com/briansmith/ring/blob/main/tests/rand_tests.rs
    open fun givenByteArray_whenNextBytes_thenIsFilledWithData() {
        val linuxLimit = 256
        val webLimit = 65536

        val sizes = listOf(
            1,
            2,
            3,
            96,
            linuxLimit - 1,
            linuxLimit,
            linuxLimit + 1,
            linuxLimit * 2,
            511,
            512,
            513,
            4096,
            webLimit - 1,
            webLimit,
            webLimit + 1,
            webLimit * 2,
        )

        for (size in sizes) {
            val bytes = ByteArray(size)
            val emptyByte = bytes[0]

            sRandom.nextBytesCopyTo(bytes)

            var emptyCount = 0
            bytes.forEach {
                if (it == emptyByte) {
                    emptyCount++
                }
            }

            // Some indices will remain empty so cannot check if all were
            // filled. Must adjust our limit depending on size to mitigate
            // false positives.
            val emptyLimit = when {
                size < 10 -> 0.5f
                size < 200 -> 0.04F
                size < 1000 -> 0.03F
                size < 10000 -> 0.01F
                else -> 0.0075F
            }.let { pctErr ->
                (size * pctErr).toInt()
            }

            val message = "size=$size,emptyLimit=$emptyLimit,emptyCount=$emptyCount"
//            println(message)

            assertTrue(
                actual = emptyCount <= emptyLimit,
                message = message
            )
        }
    }
}
