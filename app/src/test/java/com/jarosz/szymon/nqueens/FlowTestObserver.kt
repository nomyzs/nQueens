package com.jarosz.szymon.nqueens

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun <T> Flow<T>.test(coroutineScope: CoroutineScope): FlowTestObserver<T> =
        FlowTestObserver(this, coroutineScope)

class FlowTestObserver<T>(flow: Flow<T>, coroutineScope: CoroutineScope) {
    val data = mutableListOf<T>()

    init {
        coroutineScope.launch(Dispatchers.Unconfined) {
            flow.collect { data.add(it) }
        }
    }

    fun last(): T = data.last()
}
