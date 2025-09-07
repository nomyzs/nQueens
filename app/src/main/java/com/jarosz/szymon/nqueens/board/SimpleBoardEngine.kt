package com.jarosz.szymon.nqueens.board

class SimpleBoardEngine(size: Int) : BoardEngine(size) {
    private val _placedPieces = mutableSetOf<Piece>()

    override val placedPieces: Set<Piece> get() = _placedPieces

    override fun hasPiece(position: Position): Boolean =
        _placedPieces.any { it.position == position }

    override fun addPiece(piece: Piece) {
        _placedPieces.add(piece)
    }

    override fun removePiece(position: Position) {
        _placedPieces.removeIf { it.position == position }
    }

    override fun getConflicts(): List<Position> {
        val conflicts = mutableListOf<Position>()

        for (queen in _placedPieces) {
            val queenMoves = queen.getMoves(this)

            for (otherQueen in _placedPieces) {
                if (otherQueen != queen && queenMoves.contains(otherQueen.position)) {
                    conflicts.add(queen.position)
                    conflicts.add(otherQueen.position)
                }
            }
        }

        return conflicts
    }

    override fun clear() {
        _placedPieces.clear()
    }
}
