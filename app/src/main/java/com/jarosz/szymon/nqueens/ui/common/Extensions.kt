package com.jarosz.szymon.nqueens.ui.common

import com.jarosz.szymon.nqueens.board.Board
import com.jarosz.szymon.nqueens.ui.game.Cell
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

fun Int.toBoardSizeFormat(): String = "${this}x${this}"

fun Long.toDurationFormat(): String = String.format(Locale.getDefault(), "%.2f", this / 1000f)

fun Long.toDateFormat(): String {
    val date = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault())

    return date.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
}

//TODO: perhaps move to State class
fun Board.toUIBoard(): List<Cell> {
    val cells = size.generateBoard()
    val conflicts = getConflicts()

    return cells.map {
        it.copy(
                hasQueen = placedQueens.any { queen -> queen.position == it.position },
                isConflict = conflicts.contains(it.position),
        )
    }
}

fun Int.generateBoard(): List<Cell> = List(this * this) { index ->
    Cell(row = index / this, col = index % this)
}
