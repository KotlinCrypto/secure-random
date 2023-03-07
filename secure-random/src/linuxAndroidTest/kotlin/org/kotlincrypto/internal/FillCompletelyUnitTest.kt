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

import kotlinx.cinterop.set
import kotlinx.cinterop.usePinned
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FillCompletelyUnitTest {

    @Test
    fun givenFillCompletely_whenMoreThan2InvocationsNeeded_thenUpdatesIndexCorrectly() {
        listOf(
            9,
            21,
            42,
            81,
        ).forEach { size ->
            // Must be multiple of 3 to simulate more than 3 successive
            // invocations of fillCompletely.block invocation.
            assertTrue(size %3 == 0)

            val bytes = ByteArray(size)

            val fillSize = size / 3

            var invocationCount = 0
            bytes.usePinned { pinned ->
                pinned.fillCompletely(size) { ptr, _ ->
                    for (i in 0..fillSize) {
                        ptr[i] = 1
                    }

                    invocationCount++
                    fillSize
                }
            }

            bytes.forEach { byte ->
                assertEquals(1, byte)
            }

            assertEquals(3, invocationCount)
        }
    }
}
