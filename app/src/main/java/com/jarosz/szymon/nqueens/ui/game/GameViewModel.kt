package com.jarosz.szymon.nqueens.ui.game

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GameViewModel(private val boardSize: Int) : ViewModel() {

    private val _state = MutableStateFlow(_initialState)
    val state: StateFlow<GameState> = _state

    private val _initialState: GameState
        get() = GameState(boardSize, boardSize.generateBoard())

    fun placeQueen(cell: Cell) {
        val current = _state.value
        val updatedBoard = current.board.map {
            if (it.row == cell.row && it.col == cell.col) it.copy(hasQueen = !it.hasQueen)
            else it
        }
        val markedBoard = markConflicts(updatedBoard)
        val win = checkWin(markedBoard)

        _state.value = current.copy(board = markedBoard, showWinDialog = win)
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

        return queens.size == boardSize
    }

    fun onWinDialogDismiss() {
        _state.value = _state.value.copy(showWinDialog = false)
    }

    fun resetGame() {
        _state.value = _initialState
    }
}

data class GameState(val totalQueens: Int, val board: List<Cell> = emptyList(), val showWinDialog: Boolean = false) {
    val placedQueensCount: Int
        get() = board.count { it.hasQueen }
}

data class Cell(val row: Int, val col: Int, val hasQueen: Boolean = false, val isConflict: Boolean = false)

private fun Int.generateBoard(): List<Cell> = List(this * this) { index ->
    Cell(row = index / this, col = index % this)
}
