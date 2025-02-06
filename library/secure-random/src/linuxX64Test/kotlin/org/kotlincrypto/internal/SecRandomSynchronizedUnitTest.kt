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
@file:Suppress("UnnecessaryOptInAnnotation")

package org.kotlincrypto.internal

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.runTest
import kotlin.concurrent.AtomicInt
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalForeignApi::class)
class SecRandomSynchronizedUnitTest {

    private class TestSynchronized: SecRandomSynchronized() {
        private val lock = Mutex(locked = true)
        val invocationCount = AtomicInt(0)
        val loadedCount = AtomicInt(0)

        suspend fun load() {
            val calledBefore = invocationCount.value == 0

            lock.withLock {  }

            synchronizedRemember { _, _ ->
                // lambda should only ever be invoked once and
                // then always return the remembered value.
                throw IllegalArgumentException("synchronizedRemember was invoked multiple times")
            }

            if (calledBefore) {
                loadedCount.incrementAndGet()
            }
        }

        fun trigger() {
            synchronizedRemember { _, _ ->

                lock.unlock()

                runBlocking {
                    delay(500L)
                }

                true
            }

            invocationCount.incrementAndGet()
        }
    }

    @Test
    fun givenPollingResult_whenMultipleInvocations_thenOnlyPollsOnce() = runTest {
        val testSynchronized = TestSynchronized()

        val jobs = mutableListOf<Job>()

        // load up the test class which will suspend
        // until trigger is pulled
        val repeatTimes = 100
        repeat(repeatTimes) {

            launch {
                testSynchronized.load()
            }.let { job ->
                jobs.add(job)
            }
        }

        delay(500L)

        // Trigger it from background thread which
        // will block for 500ms, ensuring that all
        // the load is put onto synchronizedRemember
        launch(Dispatchers.Default) {
            testSynchronized.trigger()
        }.join()

        // Wait for everyone to finish
        for (job in jobs) {
            job.join()
        }

        assertEquals(1, testSynchronized.invocationCount.value)
        assertEquals(repeatTimes, testSynchronized.loadedCount.value)
    }
}

