package com.jarosz.szymon.nqueens.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "results")
data class GameResult(
        @PrimaryKey val timestamp: Long = 0L,
        val boardSize: Int,
        val timeMillis: Long
)
