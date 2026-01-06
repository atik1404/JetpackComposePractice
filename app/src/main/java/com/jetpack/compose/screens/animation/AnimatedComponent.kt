package com.jetpack.compose.screens.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jetpack.compose.R


@Composable
fun AnimatedExpandCollapsed(
    modifier: Modifier = Modifier,
) {
    var isExpand by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AnimatedVisibility(
            visible = isExpand,
            enter = scaleIn(initialScale = 0.2f) + fadeIn(),
            exit = fadeOut()
        ) {
            ButtonRow()
        }

        TextButton(
            onClick = {
                isExpand = !isExpand
            }
        ) {
            Text(if (isExpand) "Collapse" else "Expand")
        }
    }
}

@Composable
private fun ButtonRow() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ButtonWithTitle()
        ButtonWithTitle()
        ButtonWithTitle()
    }
}

@Composable
private fun ButtonWithTitle() {
    Column(
        modifier = Modifier.size(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_camera),
            contentDescription = null
        )
        Text("Camera")
    }
}