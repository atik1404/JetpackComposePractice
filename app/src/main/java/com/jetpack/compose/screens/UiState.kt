package com.jetpack.compose.screens

data class UiState(
    val items: List<ItemModel> = emptyList(),
    val isLoading: Boolean = false,
    val isMoreItemLoading: Boolean = false,
    val hasMorePage: Boolean = true,
    val pageNo: Int = 1
)

sealed interface UiAction {
    data object FetchItems : UiAction
    data object FetchMoreItems : UiAction
}
