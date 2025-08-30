package com.jarosz.szymon.nqueens.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarosz.szymon.nqueens.data.GameResult
import com.jarosz.szymon.nqueens.data.ResultsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(resultsRepo: ResultsRepository) : ViewModel() {
    private val _boardSize = MutableStateFlow(defaultBoardSize)
    private val resultsFlow = resultsRepo.getResults()

    val homeState: StateFlow<HomeState> = combine(_boardSize, resultsFlow) { size, results ->
        HomeState(size, results)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), _initialState)


//    _boardSize.map
//    { boardSize -> HomeState(boardSize) }
//    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), _initialState)

    private val _initialState: HomeState
        get() = HomeState(_boardSize.value)


    fun updateBoardSize(size: Int) {
        _boardSize.value = size
    }
}


data class HomeState(val boardSize: Int, val results: List<GameResult> = emptyList())

const val defaultBoardSize = 5
