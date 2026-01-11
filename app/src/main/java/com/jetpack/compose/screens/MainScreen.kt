package com.jetpack.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun MainScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.action(UiAction.FetchItems)
    }
    MainScreen(
        modifier = modifier,
        uiState = uiState,
        action = {
            viewModel.action(it)
        }
    )
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    uiState: UiState,
    action: (UiAction) -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState, uiState.items.size, 20) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisible ->
                val lastIndex = uiState.items.lastIndex
                //Timber.e("---> LastVisible: $lastVisible LastIndex: $lastIndex")
                if (lastVisible != null && lastVisible == lastIndex && uiState.hasMorePage) {
                    action.invoke(UiAction.FetchMoreItems)
                }
            }
    }
    val scope = rememberCoroutineScope()

    val jumpThreshold = with(LocalDensity.current) {
        JumpToBottomThreshold.toPx()
    }


    Box() {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        val items = uiState.items
        if (items.isNotEmpty()) {
            LazyColumn(
                state = listState,
                reverseLayout = true,
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.items.size, key = { it }) { item ->
                    ListItem("${items[item]} $item")
                }
            }
        }

//        if (uiState.isMoreItemLoading) {
//            CircularProgressIndicator()
//        }

        val jumpToBottomButtonEnabled by remember {
            derivedStateOf {
                listState.firstVisibleItemIndex != 0 ||
                        listState.firstVisibleItemScrollOffset > jumpThreshold
            }
        }

        JumpToBottom(
            // Only show if the scroller is not at the bottom
            enabled = jumpToBottomButtonEnabled,
            onClicked = {
                scope.launch {
                    listState.animateScrollToItem(0)
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

private val JumpToBottomThreshold = 56.dp

@Composable
private fun ListItem(item: String) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        Text(text = item)
        Text(text = "This is subtitle")
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    MainScreen(
        uiState = UiState(),
        action = {}
    )
}