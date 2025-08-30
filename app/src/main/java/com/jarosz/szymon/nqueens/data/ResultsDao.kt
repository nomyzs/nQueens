package com.jarosz.szymon.nqueens.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultsDao {
    @Insert
    suspend fun insert(result: GameResult)

    @Query("SELECT * FROM results ORDER BY timeMillis ASC")
    fun getResults(): Flow<List<GameResult>>
}
