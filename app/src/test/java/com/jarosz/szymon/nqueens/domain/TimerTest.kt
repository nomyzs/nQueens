package com.jarosz.szymon.nqueens.domain

import com.jarosz.szymon.nqueens.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimerTest {
    @Test
    fun `timer emits correct elapsed time after advancing time`() = runTest {
        val timer = TimerImpl(StandardTestDispatcher(this.testScheduler), this.backgroundScope)
        val ticker = timer.ticker.test(this.backgroundScope)

        timer.start(this.backgroundScope)

        advanceTimeBy(500L)
        runCurrent()

        assertTrue(ticker.last() == 500L)
    }

    @Test
    fun `timer emits multiple ticks as time advances`() = runTest {
        val timer = TimerImpl(StandardTestDispatcher(this.testScheduler))
        val ticker = timer.ticker.test(backgroundScope)
        timer.start(this.backgroundScope)

        advanceTimeBy(300L)
        runCurrent()

        val ticks = ticker.data.takeLast(3).toList()
        assertEquals(listOf(100L, 200L, 300L), ticks)
    }

    @Test
    fun `timer stops emitting after stop is called`() = runTest {
        val timer = TimerImpl(StandardTestDispatcher(this.testScheduler))
        val ticker = timer.ticker.test(backgroundScope)
        timer.start(this.backgroundScope)

        advanceTimeBy(200L)
        runCurrent()
        timer.stop()
        advanceTimeBy(200L)
        runCurrent()
        assertEquals(200L, ticker.last())
    }
}
