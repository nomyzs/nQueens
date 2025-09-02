package com.jarosz.szymon.nqueens.ui.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarosz.szymon.nqueens.board.Board
import com.jarosz.szymon.nqueens.board.Position
import com.jarosz.szymon.nqueens.data.GameResult
import com.jarosz.szymon.nqueens.data.ResultsRepository
import com.jarosz.szymon.nqueens.ui.common.generateBoard
import com.jarosz.szymon.nqueens.ui.common.toUIBoard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GameViewModel @Inject constructor(
        private val resultsRepo: ResultsRepository,
        savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _boardSize: Int = checkNotNull(savedStateHandle["boardSize"])
    private val _state = MutableStateFlow(_initialState)
    private val _timer = MutableStateFlow(0L)
    private val _board = Board(_boardSize)

    val state: StateFlow<GameState> = combine(_state, _timer) { gameState, time ->
        gameState.copy(board = gameState.board, time = time)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, _initialState)

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
        _timerJob?.cancel()
        _timerJob = viewModelScope.launch {
            while (isActive) {
                _timer.value = System.currentTimeMillis() - _startTime
                delay(100L)
            }
        }
    }

    fun placeQueen(cell: Cell) {
        if (_board.hasQueen(cell.position)) {
            _board.removeQueen(cell.position)
        } else {
            _board.addQueen(cell.position)
        }

        val uiBoard = _board.toUIBoard()

        val win = checkWin(uiBoard)
        if (win) {
            _timerJob?.cancel()
            val currentTime = System.currentTimeMillis()
            _timer.value = currentTime - _startTime
            saveGameResult()
        }

        _state.value = _state.value.copy(board = uiBoard, showWinDialog = win)
    }

    private fun saveGameResult() {
        viewModelScope.launch {
            val bestResult = resultsRepo.bestResult(_boardSize)

            if ((bestResult?.timeMillis ?: Long.MAX_VALUE) > _timer.value) {
                resultsRepo.insertResult(GameResult(_boardSize, _startTime, _timer.value))
            }
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
        _board.clear()
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

data class Cell(
        val row: Int, val col: Int, val hasQueen: Boolean = false, val isConflict: Boolean = false, val highlight: Boolean = false,
) {
    val position: Position = Position(row, col)
}

