@file:OptIn(ExperimentalCoroutinesApi::class)

package com.jarosz.szymon.nqueens.ui.game

import androidx.lifecycle.SavedStateHandle
import com.jarosz.szymon.nqueens.board.Position
import com.jarosz.szymon.nqueens.data.GameResult
import com.jarosz.szymon.nqueens.data.ResultsRepository
import com.jarosz.szymon.nqueens.domain.TimerImpl
import com.jarosz.szymon.nqueens.test
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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
    private val resultsRepo = mockk<ResultsRepository>()

    @Before
    fun setup() {
        mockkStatic("androidx.navigation.SavedStateHandleKt")
        every { savedStateHandle.get<Int>("boardSize") } returns 4

        val timer = TimerImpl(testDispatcher)
        viewModel = GameViewModel(resultsRepo, timer, savedStateHandle)
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
        val stateFlow = viewModel.state.test(backgroundScope)
        val pos = Position(0, 0)

        viewModel.placeQueen(pos)
        viewModel.placeQueen(pos)

        val state = stateFlow.last()
        assertEquals(0, state.placedQueensCount)
        assertFalse(state.board.any { it.position == pos && it.hasQueen })
    }

    @Test
    fun `game completion shows win dialog and saves result`() = runTest {
        coEvery { resultsRepo.bestResult(any()) } returns GameResult(4, 0L, Long.MAX_VALUE)
        coEvery { resultsRepo.insertResult(any()) } just Runs

        viewModel.placeQueen(Position(0, 1))
        viewModel.placeQueen(Position(1, 3))
        viewModel.placeQueen(Position(2, 0))
        viewModel.placeQueen(Position(3, 2))

        val state = viewModel.state.value
        assertTrue(state.showWinDialog)

        coVerify { resultsRepo.insertResult(any()) }
    }

    @Test
    fun `onWinDialogDismiss hides dialog`() = runTest(testDispatcher) {
        viewModel.onWinDialogDismiss()
        assertFalse(viewModel.state.value.showWinDialog)
    }
}
