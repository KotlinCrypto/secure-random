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

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.Pinned
import org.kotlincrypto.SecRandomCopyException

@OptIn(ExperimentalForeignApi::class)
internal expect abstract class SecRandomDelegate private constructor() {

    @Throws(SecRandomCopyException::class)
    internal abstract fun nextBytesCopyTo(bytes: Pinned<ByteArray>, size: Int)

    internal companion object: SecRandomDelegate {

        override fun nextBytesCopyTo(bytes: Pinned<ByteArray>, size: Int)
    }
}
