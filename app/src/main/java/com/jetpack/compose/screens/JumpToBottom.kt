/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jetpack.compose.screens

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jetpack.compose.R

private enum class Visibility {
    VISIBLE,
    GONE,
}

/**
 * Shows a button that lets the user scroll to the bottom.
 */
@Composable
fun JumpToBottom(enabled: Boolean, onClicked: () -> Unit, modifier: Modifier = Modifier) {
    // Show Jump to Bottom button
    val transition = updateTransition(
        enabled,
        label = "JumpToBottom visibility animation",
    )
    val bottomOffset by transition.animateDp(label = "JumpToBottom offset animation") {
        if (!it) {
            (-32).dp
        } else {
            32.dp
        }
    }
    if (bottomOffset > 0.dp) {
        ExtendedFloatingActionButton(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_top),
                    modifier = Modifier.height(18.dp),
                    contentDescription = null,
                )
            },
            text = {
                Text(text = "Jump to Bottom")
            },
            onClick = onClicked,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = modifier
                .offset(x = 0.dp, y = -bottomOffset)
                .height(36.dp),
        )
    }
}

@Preview
@Composable
fun JumpToBottomPreview() {
    JumpToBottom(enabled = true, onClicked = {})
}
