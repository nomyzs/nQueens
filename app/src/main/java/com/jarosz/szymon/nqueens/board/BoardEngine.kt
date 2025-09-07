package com.jarosz.szymon.nqueens.board

data class Position(val row: Int, val col: Int)

class Queen(val position: Position) {
    fun getMoves(boardEngine: BoardEngine): List<Position> {
        val directions = orthogonalDirections + diagonalDirections
        val moves = mutableListOf<Position>()
        val maxSteps = boardEngine.size - 1

        for (direction in directions) {
            var step = 1
            var nextRow = position.row + direction.rowDelta
            var nextCol = position.col + direction.colDelta
            var nextPosition = Position(nextRow, nextCol)

            while (step <= maxSteps && boardEngine.isInside(nextPosition)) {
                moves.add(Position(nextRow, nextCol))
                step++
                nextRow += direction.rowDelta
                nextCol += direction.colDelta
                nextPosition = Position(nextRow, nextCol)
            }
        }

        return moves
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Queen

        return position == other.position
    }

    override fun hashCode(): Int {
        return position.hashCode()
    }
}

class BoardEngine(val size: Int) {
    private val _placedQueens = mutableSetOf<Queen>()

    val placedQueens: Set<Queen> get() = _placedQueens

    fun hasQueen(pos: Position): Boolean =
            _placedQueens.any { it.position == pos }

    fun isInside(pos: Position): Boolean =
            pos.row in 0 until size && pos.col in 0 until size

    fun addQueen(position: Position) {
        _placedQueens.add(Queen(position))
    }

    fun removeQueen(position: Position) {
        _placedQueens.removeIf { it.position == position }
    }

    fun getConflicts(): List<Position> {
        val conflicts = mutableListOf<Position>()

        for (queen in _placedQueens) {
            val queenMoves = queen.getMoves(this)

            for (otherQueen in _placedQueens) {
                if (otherQueen != queen && queenMoves.contains(otherQueen.position)) {
                    conflicts.add(queen.position)
                    conflicts.add(otherQueen.position)
                }
            }
        }

        return conflicts
    }

    fun clear() {
        _placedQueens.clear()
    }
}

val orthogonalDirections = listOf(
        Direction.UP,
        Direction.DOWN,
        Direction.LEFT,
        Direction.RIGHT,
)
val diagonalDirections = listOf(
        Direction.UP_LEFT,
        Direction.UP_RIGHT,
        Direction.DOWN_LEFT,
        Direction.DOWN_RIGHT,
)

enum class Direction(val rowDelta: Int, val colDelta: Int) {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1),
    UP_LEFT(-1, -1),
    UP_RIGHT(-1, 1),
    DOWN_LEFT(1, -1),
    DOWN_RIGHT(1, 1)
}
