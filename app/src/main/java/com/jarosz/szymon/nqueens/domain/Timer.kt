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

interface Timer {
    fun start(viewModelScope: CoroutineScope)
    fun stop()
    val ticker: Flow<Long>
}

class TimerImpl @Inject constructor(
    private val dispatcher: CoroutineDispatcher
) : Timer {
    private var job: Job? = null
    private val _ticker = MutableSharedFlow<Long>()
    private var elapsed: Long = 0L

    override val ticker: Flow<Long>
        get() = _ticker

    override fun start(viewModelScope: CoroutineScope) {
        val scope = viewModelScope

        stop()
        elapsed = 0L
        job = scope.launch(dispatcher) {
            while (isActive) {
                _ticker.emit(elapsed)
                delay(INTERVAL)
                elapsed += INTERVAL
            }
        }
    }

    override fun stop() {
        job?.cancel()
    }

}

private const val INTERVAL = 50L
