package com.jetpack.compose.screens

data class UiState(
    val items: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isMoreItemLoading: Boolean = false,
    val pageNo: Int = 0
)

sealed interface UiAction {
    data object FetchItems : UiAction
    data object FetchMoreItems : UiAction
}
