package com.jarosz.szymon.nqueens.ui.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarosz.szymon.nqueens.data.GameResult
import com.jarosz.szymon.nqueens.data.ResultsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GameViewModel @Inject constructor(val resultsRepo: ResultsRepository, savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _boardSize: Int = checkNotNull(savedStateHandle["boardSize"])
    private val _state = MutableStateFlow(_initialState)
    private val _timer = MutableStateFlow(0L)

    val state: StateFlow<GameState> = combine(_state, _timer) { gameState, time ->
        gameState.copy(board = gameState.board, time = time)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), _initialState)

    private var _startTime: Long = System.currentTimeMillis()
    private var _timerJob: Job? = null

    init {
        startTimer()
    }

    private val _initialState: GameState
        get() = GameState(_boardSize, _boardSize.generateBoard())

    private fun startTimer() {
        _timer.value = 0L
        _startTime = System.currentTimeMillis()
        _timerJob = viewModelScope.launch {
            while (true) {
                val currentTime = System.currentTimeMillis()
                _timer.value = currentTime - _startTime!!
                delay(1000L)
            }
        }
    }

    fun placeQueen(cell: Cell) {
        val current = _state.value
        val updatedBoard = current.board.map {
            if (it.row == cell.row && it.col == cell.col) it.copy(hasQueen = !it.hasQueen)
            else it
        }
        val markedBoard = markConflicts(updatedBoard)
        val win = checkWin(markedBoard)
        if (win) {
            _timerJob?.cancel()
            saveGameResult()
        }

        _state.value = current.copy(board = markedBoard, showWinDialog = win)
    }

    private fun saveGameResult() {
        viewModelScope.launch {
            resultsRepo.insertResult(GameResult(_startTime, _boardSize, _timer.value))
        }
    }

    private fun markConflicts(board: List<Cell>): List<Cell> {
        val queens = board.filter { it.hasQueen }

        return board.map { cell ->
            if (cell.hasQueen) {
                val conflict = queens.any {
                    it != cell && (it.row == cell.row || it.col == cell.col ||
                            kotlin.math.abs(it.row - cell.row) == kotlin.math.abs(it.col - cell.col))
                }
                cell.copy(isConflict = conflict)
            } else cell.copy(isConflict = false)
        }
    }

    private fun checkWin(board: List<Cell>): Boolean {
        val queens = board.filter { it.hasQueen && !it.isConflict }

        return queens.size == _boardSize
    }

    fun onWinDialogDismiss() {
        _state.value = _state.value.copy(showWinDialog = false)
    }

    fun resetGame() {
        _state.value = _initialState
        startTimer()
    }
}

data class GameState(
        val boardSize: Int,
        val board: List<Cell> = emptyList(),
        val showWinDialog: Boolean = false,
        val time: Long = 0L
) {
    val placedQueensCount: Int
        get() = board.count { it.hasQueen }
}

data class Cell(val row: Int, val col: Int, val hasQueen: Boolean = false, val isConflict: Boolean = false)

private fun Int.generateBoard(): List<Cell> = List(this * this) { index ->
    Cell(row = index / this, col = index % this)
}
