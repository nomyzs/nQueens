package com.jarosz.szymon.nqueens.ui.game

import com.jarosz.szymon.nqueens.board.Position

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
