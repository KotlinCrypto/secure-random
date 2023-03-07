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

import kotlinx.cinterop.ByteVarOf
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKStringFromUtf8
import platform.windows.DWORD
import platform.windows.FORMAT_MESSAGE_FROM_SYSTEM
import platform.windows.FORMAT_MESSAGE_IGNORE_INSERTS
import platform.windows.LANG_NEUTRAL
import platform.windows.SUBLANG_DEFAULT
import platform.windows.FormatMessageA

/* https://github.com/square/okio/blob/master/okio/src/mingwX64Main/kotlin/okio/-Windows.kt */
internal fun errorToString(error: DWORD): String {
    memScoped {
        val messageMaxSize = 2048
        val message = allocArray<ByteVarOf<Byte>>(messageMaxSize)
        FormatMessageA(
            dwFlags = (FORMAT_MESSAGE_FROM_SYSTEM or FORMAT_MESSAGE_IGNORE_INSERTS).toUInt(),
            lpSource = null,
            dwMessageId = error,
            dwLanguageId = (SUBLANG_DEFAULT * 1024 + LANG_NEUTRAL).toUInt(), // MAKELANGID macro.
            lpBuffer = message,
            nSize = messageMaxSize.toUInt(),
            Arguments = null
        )
        return message.toKStringFromUtf8().trim()
    }
}
