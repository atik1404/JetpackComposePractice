package com.jetpack.compose.screens

import com.jetpack.compose.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MainViewModel @Inject constructor() : BaseViewModel() {

    private val _uiState = MutableStateFlow(UiState(items = emptyList()))
    val uiState = _uiState.asStateFlow()
    private val pageSize = 15

    val action: (UiAction) -> Unit = {
        when (it) {
            UiAction.FetchItems -> fetchItems()
            UiAction.FetchMoreItems -> fetchMoreItems()
        }
    }

    private fun fetchItems() {
        execute {
            _uiState.value = _uiState.value.copy(isLoading = true, pageNo = 1)
            delay(2000)
            val items = getItems()
            val hasMorePage = items.size >= pageSize
            _uiState.value = _uiState.value.copy(items = items, isLoading = false, hasMorePage = hasMorePage)
        }
    }

    private fun fetchMoreItems() {
        execute {
            val currentPage = _uiState.value.pageNo

            _uiState.value =
                _uiState.value.copy(isMoreItemLoading = true, pageNo = (currentPage + 1))

            Timber.e("---> Load next page: ${_uiState.value.pageNo}")
            delay(2000)
            val items = getItems()

            val hasMorePage = items.size >= pageSize
            val currentItems = _uiState.value.items
            val updatedItems = currentItems + items
            _uiState.value = _uiState.value.copy(
                items = updatedItems,
                isMoreItemLoading = false,
                hasMorePage = hasMorePage
            )
        }
    }

    fun getItems(): List<String> {
        val size = Random.nextInt(50)
        Timber.e("---> Size: $size")
        return (1..50).map { "This is title for index: " }
    }
}