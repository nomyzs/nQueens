package com.jarosz.szymon.nqueens.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ResultsRepository {
    fun getResults(): Flow<List<GameResult>>

    suspend fun insertResult(result: GameResult)
}


class ResultsRepositoryImpl @Inject constructor(private val dao: ResultsDao) : ResultsRepository {
    override fun getResults(): Flow<List<GameResult>> = dao.getResults()

    override suspend fun insertResult(result: GameResult) = dao.insert(result)
}
