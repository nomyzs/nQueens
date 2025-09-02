package com.jarosz.szymon.nqueens.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class Timer @Inject constructor(
        dispatcher: CoroutineDispatcher
) {
    private val coroutineScope = CoroutineScope(dispatcher)
    private var job: Job? = null

    private val _ticker = MutableStateFlow(0L)
    val ticker: Flow<Long> = _ticker
    private var _startTime: Long = System.currentTimeMillis()

    fun start() {
        stop()
        _startTime = System.currentTimeMillis()
        job = coroutineScope.launch {
            while (isActive) {
                delay(100L)
                _ticker.value = System.currentTimeMillis() - _startTime
            }
        }
    }

    fun stop() {
        job?.cancel()
    }
}
