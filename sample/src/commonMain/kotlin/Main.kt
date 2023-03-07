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

import org.kotlincrypto.SecRandomCopyException
import org.kotlincrypto.SecureRandom

fun main() {
    val sRandom = SecureRandom()

    for (i in 10..20) {
        val bytes = try {
            sRandom.nextBytesOf(i).toList()
        } catch (e: SecRandomCopyException) {
            e.printStackTrace()
            return
        }

        println("$i: $bytes")
    }

    listOf(
        500,
        5000,
        50000,
    ).forEach { i ->

        try {
            sRandom.nextBytesOf(i)
        } catch (e: SecRandomCopyException) {
            e.printStackTrace()
            return
        }

        println("$i: omitted (success)")
    }
}
