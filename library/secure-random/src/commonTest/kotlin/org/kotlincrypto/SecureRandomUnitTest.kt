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

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * See [EnsureFilledHelper]
 * */
class SecureRandomUnitTest: EnsureFilledHelper() {

    override val sRandom = SecureRandom()

    @Test
    fun givenNextBytesOf_whenCountNegative_thenThrows() {
        try {
            sRandom.nextBytesOf(-1)
            fail()
        } catch (_: IllegalArgumentException) {
            // pass
        }
    }

    @Test
    fun givenNextBytesOf_whenCount0_thenReturnsEmpty() {
        assertTrue(sRandom.nextBytesOf(0).isEmpty())
    }

    @Test
    override fun givenByteArray_whenNextBytes_thenIsFilledWithData() {
        super.givenByteArray_whenNextBytes_thenIsFilledWithData()
    }
}
