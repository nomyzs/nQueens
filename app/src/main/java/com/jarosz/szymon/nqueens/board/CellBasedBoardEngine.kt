package com.jarosz.szymon.nqueens.board

/**
 * Unused implementation of BoardEngine that uses a list of cells to manage pieces on the board.
 * Left the experiment here for reference. Offers simpler mapping to UI layer, potentially
 * removes duplication of board generation logic and decouples position from [Piece] class.
 * */
class CellBasedBoardEngine(size: Int) : BoardEngine(size) {
    private val _boardCells = MutableList(size * size) { index ->
        BoardCell(position = Position(row = index / size, col = index % size))
    }

    val boardCells: List<BoardCell>
        get() = _boardCells

    override val placedPieces: Set<Piece>
        get() = _boardCells.mapNotNull { it.piece }.toSet()

    private fun findCell(position: Position): BoardCell? =
        _boardCells.find { it.position == position }

    override fun hasPiece(position: Position): Boolean {
        return findCell(position)?.hasPiece ?: false
    }

    override fun addPiece(piece: Piece) {
        val idx = _boardCells.indexOfFirst { it.position == piece.position }
        if (idx != -1) {
            _boardCells[idx] = _boardCells[idx].copy(piece = piece)
        }
    }

    override fun removePiece(position: Position) {
        val idx = _boardCells.indexOfFirst { it.position == position }
        if (idx != -1) {
            _boardCells[idx] = _boardCells[idx].copy(piece = null)
        }
    }

    override fun getConflicts(): List<Position> {
        val conflicts = mutableListOf<Position>()
        val cellWithPieces = _boardCells.filter { it.piece != null }

        for (cell in cellWithPieces) {
            val pieceMoves = cell.piece!!.getMoves(this)

            for (otherCell in cellWithPieces) {
                if (otherCell != cell && pieceMoves.contains(otherCell.position)) {
                    conflicts.add(cell.position)
                    conflicts.add(otherCell.position)
                }
            }
        }

        return conflicts
    }

    override fun clear() {
        _boardCells.onEach { it.copy(piece = null) }
    }
}

data class BoardCell(
    val position: Position,
    val piece: Piece? = null,
) {
    val hasPiece: Boolean
        get() = piece != null
}
