package com.jetpack.compose.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


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
                    // action.invoke(UiAction.FetchMoreItems)
                }
            }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        TextButton(
            onClick = {
                action.invoke(UiAction.FetchItems)
            }
        ) {
            Text("Reload")
        }

        val items = uiState.items
        if (items.isNotEmpty()) {
            LazyColumn(
                //state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(uiState.items, key = { _, item -> item.id }) { index, item ->
                    val animationDelay = index * 1000

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem(
                                fadeInSpec = tween(
                                    durationMillis = 500,
                                    delayMillis = animationDelay // This creates the "one by one" effect
                                ),
                                placementSpec = spring(stiffness = Spring.StiffnessLow),
                            )
                    ) {
                        ListItem("${items[index]} ${item.value}")
                    }
                }

            }
        }

        if (uiState.isMoreItemLoading) {
            CircularProgressIndicator()
        }
    }
}

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