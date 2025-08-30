package com.jarosz.szymon.nqueens.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel : ViewModel() {
    private val _boardSize = MutableStateFlow(defaultBoardSize)

    val homeState: StateFlow<HomeState> = _boardSize.map { boardSize -> HomeState(boardSize) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, _initialState)

    private val _initialState: HomeState
        get() = HomeState(_boardSize.value)


    fun updateBoardSize(size: Int) {
        _boardSize.value = size
    }
}


data class HomeState(val boardSize: Int)

const val defaultBoardSize = 5
