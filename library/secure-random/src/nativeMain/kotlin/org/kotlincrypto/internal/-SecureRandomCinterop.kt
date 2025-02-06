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
package org.kotlincrypto.internal

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKStringFromUtf8
import org.kotlincrypto.SecRandomCopyException
import platform.posix.strerror

@OptIn(ExperimentalForeignApi::class)
internal fun errnoToSecRandomCopyException(errno: Int): SecRandomCopyException {
    val message = strerror(errno)?.toKStringFromUtf8() ?: "errno: $errno"
    return SecRandomCopyException(message)
}
