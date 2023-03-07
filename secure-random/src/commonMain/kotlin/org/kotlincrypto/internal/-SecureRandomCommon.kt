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
@file:Suppress("KotlinRedundantDiagnosticSuppress")

package org.kotlincrypto.internal

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import org.kotlincrypto.SecRandomCopyException
import org.kotlincrypto.SecureRandom

@Suppress("NOTHING_TO_INLINE")
@Throws(IllegalArgumentException::class, SecRandomCopyException::class)
internal inline fun SecureRandom.commonNextBytesOf(count: Int): ByteArray {
    require(count >= 0) { "count cannot be negative" }
    val bytes = ByteArray(count)
    nextBytesCopyTo(bytes)
    return bytes
}

@OptIn(ExperimentalContracts::class)
@Suppress("NOTHING_TO_INLINE")
internal inline fun ByteArray?.ifNotNullOrEmpty(block: ByteArray.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    if (this == null || this.isEmpty()) return
    block.invoke(this)
}
