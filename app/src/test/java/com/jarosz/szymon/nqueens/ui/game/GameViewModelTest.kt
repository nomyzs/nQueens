@file:OptIn(ExperimentalCoroutinesApi::class)

package com.jarosz.szymon.nqueens.ui.game

import androidx.lifecycle.SavedStateHandle
import com.jarosz.szymon.nqueens.board.Position
import com.jarosz.szymon.nqueens.data.GameResult
import com.jarosz.szymon.nqueens.data.ResultsRepository
import com.jarosz.szymon.nqueens.domain.Timer
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GameViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: GameViewModel

    private val savedStateHandle = mockk<SavedStateHandle>()
    private val resultsRepo = mockk<ResultsRepository>(relaxed = true)
    private val timer = TimerMock()


    @Before
    fun setup() {
        mockkStatic("androidx.navigation.SavedStateHandleKt")
        every { savedStateHandle.get<Int>("boardSize") } returns 4
        viewModel = GameViewModel(resultsRepo, timer, testDispatcher, savedStateHandle)
    }

    @Test
    fun `initial state has correct board size`() = runTest(testDispatcher) {
        val state = viewModel.state.value
        assertEquals(4, state.boardSize)
        assertEquals(0, state.placedQueensCount)
        assertFalse(state.showWinDialog)
    }

    @Test
    fun `placeQueen adds queen and updates state`() = runTest(testDispatcher) {
        val pos = Position(0, 0)
        viewModel.placeQueen(pos)
        val state = viewModel.state.value
        assertEquals(1, state.placedQueensCount)
        assertTrue(state.board.any { it.position == pos && it.hasQueen })
    }

    @Test
    fun `placeQueen removes queen if already present`() = runTest(testDispatcher) {
        val pos = Position(0, 0)

        viewModel.placeQueen(pos)
        viewModel.placeQueen(pos)
        val state = viewModel.state.value

        assertEquals(0, state.placedQueensCount)
        assertFalse(state.board.any { it.position == pos && it.hasQueen })
    }

    @Test
    fun `game completion shows win dialog and saves result`() = runTest(testDispatcher) {
        coEvery { resultsRepo.bestResult(any()) } returns GameResult(4, 0L, Long.MAX_VALUE)

        viewModel.placeQueen(Position(0, 1))
        viewModel.placeQueen(Position(1, 3))
        viewModel.placeQueen(Position(2, 0))
        timer.valueFlow.value = 550L
        advanceUntilIdle()
        viewModel.placeQueen(Position(3, 2))
        advanceUntilIdle()
        val state = viewModel.state.value

        assertTrue(state.showWinDialog)
        coVerify { resultsRepo.insertResult(match { it.boardSize == 4 && it.timeMillis == 550L }) }
    }

    @Test
    fun `game completion shows win dialog but doesn't save worse result`() = runTest(testDispatcher) {
        coEvery { resultsRepo.bestResult(any()) } returns GameResult(4, 0L, 500L)

        viewModel.placeQueen(Position(0, 1))
        viewModel.placeQueen(Position(1, 3))
        viewModel.placeQueen(Position(2, 0))
        timer.valueFlow.value = 600L
        advanceUntilIdle()
        viewModel.placeQueen(Position(3, 2))
        advanceUntilIdle()
        val state = viewModel.state.value

        assertTrue(state.showWinDialog)
        coVerify(exactly = 0) { resultsRepo.insertResult(any()) }
    }

    @Test
    fun `onWinDialogDismiss hides dialog`() = runTest(testDispatcher) {
        viewModel.placeQueen(Position(0, 1))
        viewModel.placeQueen(Position(1, 3))
        viewModel.placeQueen(Position(2, 0))
        viewModel.placeQueen(Position(3, 2))
        advanceUntilIdle()
        val state = viewModel.state.value
        assertTrue(state.showWinDialog)

        viewModel.onWinDialogDismiss()

        assertFalse(viewModel.state.value.showWinDialog)
    }

    @Test
    fun `resetGame clears board and restarts timer`() = runTest(testDispatcher) {
        viewModel.placeQueen(Position(0, 0))
        assertEquals(1, viewModel.state.value.placedQueensCount)

        timer.valueFlow.value = 1000L
        advanceUntilIdle()
        viewModel.resetGame()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(0, state.placedQueensCount)
        assertEquals(0L, state.time)
    }
}

class TimerMock : Timer {
    var valueFlow = MutableStateFlow(0L)

    override val ticker: Flow<Long>
        get() = valueFlow

    override fun start(viewModelScope: CoroutineScope) {
        valueFlow.value = 0L
    }

    override fun stop() {}
}
