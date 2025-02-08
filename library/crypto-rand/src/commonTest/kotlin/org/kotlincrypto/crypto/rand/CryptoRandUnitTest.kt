/*
 * Copyright (c) 2025 Matthew Nelson
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
package org.kotlincrypto.crypto.rand

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

open class CryptoRandUnitTest {

    protected open val cryptoRand: CryptoRand = CryptoRand.Default

    // https://github.com/briansmith/ring/blob/main/tests/rand_tests.rs
    @Test
    fun givenArray_whenNextBytes_thenIsFilledWithData() {
        val linuxLimit = 256
        val webLimit = 65536

        listOf(
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
            // To ensure remainder logic is working properly for JS/WasmJS
            (webLimit * 2) + 6_000,
        ).forEach { size ->
            val zeroCount = cryptoRand.nextBytes(buf = ByteArray(size)).count { it == 0.toByte() }

            // Some indices will be populated with 0, so cannot check if *all* were
            // filled. Must adjust allowable limits depending on size requested to
            // mitigate false positives.
            val zeroAllowed = when {
                size < 10 -> 0.5f
                size < 200 -> 0.04F
                size < 1000 -> 0.03F
                size < 10000 -> 0.01F
                else -> 0.0075F
            }.let { percentError -> (size * percentError).toInt() }

            val message = "size=$size, zeroAllowed=$zeroAllowed, zeroCount=$zeroCount"
//            println(message)

            assertTrue(zeroCount <= zeroAllowed, message)
        }
    }

    @Test
    fun givenEmpty_whenNextBytes_thenIsIgnored() {
        assertEquals(0, cryptoRand.nextBytes(ByteArray(0)).size)
    }
}
