/*
 * Copyright (c) 2024 Matthew Nelson
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
@file:Suppress("ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.kotlincrypto

import org.kotlincrypto.internal.commonNextBytesOf
import org.kotlincrypto.internal.ifNotNullOrEmpty
import kotlin.wasm.WasmImport
import kotlin.wasm.unsafe.UnsafeWasmMemoryApi
import kotlin.wasm.unsafe.withScopedMemoryAllocator

/**
 * A cryptographically strong random number generator (RNG).
 * */
public actual class SecureRandom public actual constructor() {

    /**
     * Returns a [ByteArray] of size [count], filled with
     * securely generated random data.
     *
     * @throws [IllegalArgumentException] if [count] is negative.
     * @throws [SecRandomCopyException] if [nextBytesCopyTo] failed.
     * */
    public actual fun nextBytesOf(count: Int): ByteArray = commonNextBytesOf(count)

    /**
     * Fills a [ByteArray] with securely generated random data.
     * Does nothing if [bytes] is null or empty.
     *
     * @throws [SecRandomCopyException] if procurement of securely random data failed.
     * */
    public actual fun nextBytesCopyTo(bytes: ByteArray?) {
        bytes.ifNotNullOrEmpty {
            @OptIn(UnsafeWasmMemoryApi::class)
            withScopedMemoryAllocator { mem ->
                val ptr = mem.allocate(size)
                val result = getRandom(ptr.address.toInt(), size)
                if (result != 0) {
                    throw SecRandomCopyException("failed to obtain bytes. errno: $result")
                }

                for (i in indices) {
                    this[i] = (ptr + i).loadByte()
                }
            }
        }
    }
}

/** [docs](https://github.com/WebAssembly/WASI/blob/main/legacy/preview1/docs.md#-random_getbuf-pointeru8-buf_len-size---result-errno) */
@Suppress("LocalVariableName")
@WasmImport("wasi_snapshot_preview1", "random_get")
private external fun getRandom(buf: Int, buf_len: Int): Int
