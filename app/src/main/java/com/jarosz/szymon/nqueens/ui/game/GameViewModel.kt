package com.jarosz.szymon.nqueens.ui.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarosz.szymon.nqueens.board.BoardEngine
import com.jarosz.szymon.nqueens.board.Position
import com.jarosz.szymon.nqueens.data.GameResult
import com.jarosz.szymon.nqueens.data.ResultsRepository
import com.jarosz.szymon.nqueens.di.DefaultDispatcher
import com.jarosz.szymon.nqueens.domain.Timer
import com.jarosz.szymon.nqueens.ui.common.generateUIBoard
import com.jarosz.szymon.nqueens.ui.common.isGameCompleted
import com.jarosz.szymon.nqueens.ui.common.toUIBoard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GameViewModel @Inject constructor(
        private val resultsRepo: ResultsRepository,
        private val timer: Timer,
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
        savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _boardSize: Int = checkNotNull(savedStateHandle["boardSize"])
    private val _state = MutableStateFlow(_initialState)
    private val _boardEngine = BoardEngine(_boardSize)

    val state: StateFlow<GameState> = _state.asStateFlow()

    init {
        timer.ticker.onEach { _state.value = _state.value.copy(time = it) }
                .flowOn(defaultDispatcher)
                .launchIn(viewModelScope)
        timer.start(viewModelScope)
    }

    private val _initialState: GameState
        get() = GameState(_boardSize, _boardSize.generateUIBoard())

    fun placeQueen(position: Position) {
        if (_boardEngine.hasQueen(position)) {
            _boardEngine.removeQueen(position)
        } else if (_boardEngine.placedQueens.size < _boardSize) {
            _boardEngine.addQueen(position)
        } else {
            return
        }

        // Conflicts extracted here to be calculated only once
        val conflicts = _boardEngine.getConflicts()
        val completed = _boardEngine.isGameCompleted(conflicts)

        if (completed) {
            timer.stop()
            saveGameResult(_state.value.time)
        }

        val board = _boardEngine.toUIBoard(conflicts)
        _state.value = _state.value.copy(board = board, showWinDialog = completed)
    }

    private fun saveGameResult(time: Long) {
        viewModelScope.launch {
            val bestResult = resultsRepo.bestResult(_boardSize)

            if ((bestResult?.timeMillis ?: Long.MAX_VALUE) > time) {
                resultsRepo.insertResult(GameResult(_boardSize, System.currentTimeMillis(), time))
            }
        }
    }

    fun winDialogDismiss() {
        _state.value = _state.value.copy(showWinDialog = false)
    }

    fun resetGame() {
        _state.value = _initialState
        _boardEngine.clear()
        timer.start(viewModelScope)
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

