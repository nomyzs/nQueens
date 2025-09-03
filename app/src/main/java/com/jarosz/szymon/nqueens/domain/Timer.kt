package com.jarosz.szymon.nqueens.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class Timer @Inject constructor(
        private val dispatcher: CoroutineDispatcher,
        private val testScope: CoroutineScope? = null
) {
    private var job: Job? = null
    private val _ticker = MutableSharedFlow<Long>()
    val ticker: Flow<Long> = _ticker
    private var elapsed: Long = 0L


    fun start(viewModelScope: CoroutineScope) {
        val scope = testScope ?: viewModelScope

        stop()
        elapsed = 0L
        job = scope.launch(dispatcher) {
            while (isActive) {
                _ticker.emit(elapsed)
                delay(100L)
                elapsed += 100L
            }
        }
    }

    fun stop() {
        job?.cancel()
    }
}
