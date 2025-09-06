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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GameViewModelTest {

    private lateinit var scheduler: TestCoroutineScheduler
    private lateinit var dispatcher: TestDispatcher
    private lateinit var scope: TestScope

    private lateinit var viewModel: GameViewModel
    private val testDispatcher = StandardTestDispatcher()
    lateinit var testScope: TestScope

    private val savedStateHandle = mockk<SavedStateHandle>()
    private val resultsRepo = mockk<ResultsRepository>()

    @Before
    fun setup() {
        scheduler = TestCoroutineScheduler()
        dispatcher = StandardTestDispatcher(scheduler, name = "OwnDispatcher")
        scope = TestScope(dispatcher)

        mockkStatic("androidx.navigation.SavedStateHandleKt")
        every { savedStateHandle.get<Int>("boardSize") } returns 4
        testScope = TestScope(testDispatcher)
        Dispatchers.setMain(testDispatcher)

        val timer = TimerImpl(testDispatcher, testScope.backgroundScope)
        viewModel = GameViewModel(resultsRepo, timer, savedStateHandle)
    }

    @After
    fun cleanUp() {
        scope.cancel()
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has correct board size`() = runTest(scheduler) {
        val state = viewModel.state.value
        assertEquals(4, state.boardSize)
        assertEquals(0, state.placedQueensCount)
        assertFalse(state.showWinDialog)
    }

    @Test
    fun `placeQueen adds queen and updates state`() = runTest(scheduler) {
        val pos = Position(0, 0)
        viewModel.placeQueen(pos)
        elapseABit()
        val state = viewModel.state.value
        assertEquals(1, state.placedQueensCount)
        assertTrue(state.board.any { it.position == pos && it.hasQueen })
    }

    @Test
    fun `placeQueen removes queen if already present`() = runTest(scheduler) {
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

        elapseABit()
        val state = viewModel.state.value
        assertTrue(state.showWinDialog)

        coVerify { resultsRepo.insertResult(any()) }
    }

    @Test
    fun `onWinDialogDismiss hides dialog`() = runTest(scheduler) {
        viewModel.onWinDialogDismiss()
        assertFalse(viewModel.state.value.showWinDialog)
    }

    private fun TestScope.elapseABit() {
        advanceTimeBy(1000L)
        runCurrent()
    }
}
