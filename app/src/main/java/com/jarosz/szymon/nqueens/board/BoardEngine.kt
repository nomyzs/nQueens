package com.jarosz.szymon.nqueens.board

data class Position(val row: Int, val col: Int)

enum class PieceType {
    QUEEN,
    ROOK
}

data class Piece(val position: Position, val type: PieceType) {
    fun getMoves(boardEngine: BoardEngine): List<Position> {
        return when (type) {
            PieceType.QUEEN -> QueenMovement().getMoves(position, boardEngine)
            PieceType.ROOK -> RookMovement().getMoves(position, boardEngine)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Piece

        return position == other.position
    }

    override fun hashCode(): Int {
        return position.hashCode()
    }
}

abstract class BoardEngine(val size: Int) {

    abstract val placedPieces: Set<Piece>

    abstract fun hasPiece(position: Position): Boolean

    fun isInside(pos: Position): Boolean =
        pos.row in 0 until size && pos.col in 0 until size

    abstract fun addPiece(piece: Piece)

    abstract fun removePiece(position: Position)

    abstract fun getConflicts(): List<Position>

    abstract fun clear()
}

