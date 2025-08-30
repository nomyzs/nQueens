package com.jarosz.szymon.nqueens.ui.game

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GameViewModel(private val boardSize: Int) : ViewModel() {

    private val _state = MutableStateFlow(_initialState)
    val state: StateFlow<GameState> = _state

    private val _initialState: GameState
        get() = GameState(boardSize.generateBoard())

    fun placeQueen(cell: Cell) {
        val current = _state.value
        val updatedBoard = current.board.map {
            if (it.row == cell.row && it.col == cell.col) it.copy(hasQueen = !it.hasQueen)
            else it
        }
        val markedBoard = markConflicts(updatedBoard)

        _state.value = current.copy(board = markedBoard)
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
}

data class GameState(val board: List<Cell> = emptyList())

data class Cell(val row: Int, val col: Int, val hasQueen: Boolean = false, val isConflict: Boolean = false)

private fun Int.generateBoard(): List<Cell> = List(this * this) { index ->
    Cell(row = index / this, col = index % this)
}
