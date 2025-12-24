package com.jetpack.compose.screens

import com.jetpack.compose.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : BaseViewModel() {

    private val _uiState = MutableStateFlow(UiState(items = emptyList()))
    val uiState = _uiState.asStateFlow()

    val action: (UiAction) -> Unit = {
        when (it) {
            UiAction.FetchItems -> fetchItems()
            UiAction.FetchMoreItems -> fetchMoreItems()
        }
    }

    private fun fetchItems() {
        execute {
            _uiState.value = _uiState.value.copy(isLoading = true, pageNo = 0)
            val items = getItems()
            _uiState.value = _uiState.value.copy(items = items, isLoading = false)
        }
    }

    private fun fetchMoreItems() {
        execute {
            val currentPage = _uiState.value.pageNo

            _uiState.value = _uiState.value.copy(isLoading = true, pageNo = (currentPage + 1))
            val items = getItems()
            _uiState.value = _uiState.value.copy(items = items, isLoading = false)
        }
    }

    fun getItems(): List<String> = (1..10).map { "This is title for index: " }
}