package com.jarosz.szymon.nqueens.ui.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarosz.szymon.nqueens.board.BoardEngine
import com.jarosz.szymon.nqueens.board.Position
import com.jarosz.szymon.nqueens.data.GameResult
import com.jarosz.szymon.nqueens.data.ResultsRepository
import com.jarosz.szymon.nqueens.ui.common.generateUIBoard
import com.jarosz.szymon.nqueens.ui.common.isGameCompleted
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
    private val _boardEngine = BoardEngine(_boardSize)

    val state: StateFlow<GameState> = combine(_state, _timer) { gameState, time ->
        gameState.copy(board = gameState.board, time = time)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, _initialState)

    private var _startTime: Long = System.currentTimeMillis()
    private var _timerJob: Job? = null

    init {
        startTimer()
    }

    private val _initialState: GameState
        get() = GameState(_boardSize, _boardSize.generateUIBoard())

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
        if (_boardEngine.hasQueen(cell.position)) {
            _boardEngine.removeQueen(cell.position)
        } else if (_boardEngine.placedQueens.size < _boardSize) {
            _boardEngine.addQueen(cell.position)
        } else {
            return
        }

        // Conflicts extracted here to be calculated only once
        val conflicts = _boardEngine.getConflicts()
        val completed = _boardEngine.isGameCompleted(conflicts)

        if (completed) {
            _timerJob?.cancel()
            val currentTime = System.currentTimeMillis()
            _timer.value = currentTime - _startTime
            saveGameResult()
        }

        _state.value = _state.value.copy(board = _boardEngine.toUIBoard(conflicts), showWinDialog = completed)
    }

    private fun saveGameResult() {
        viewModelScope.launch {
            val bestResult = resultsRepo.bestResult(_boardSize)

            if ((bestResult?.timeMillis ?: Long.MAX_VALUE) > _timer.value) {
                resultsRepo.insertResult(GameResult(_boardSize, _startTime, _timer.value))
            }
        }
    }

    fun onWinDialogDismiss() {
        _state.value = _state.value.copy(showWinDialog = false)
    }

    fun resetGame() {
        _state.value = _initialState
        _boardEngine.clear()
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
        val position: Position,
        val hasQueen: Boolean = false,
        val isConflict: Boolean = false,
        val highlight: Boolean = false,
)

