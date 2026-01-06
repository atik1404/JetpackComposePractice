package com.jetpack.compose.screens.animation

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

enum class Anchor { Left, Right }

@Composable
fun DragIconButtonRightToLeft(
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(horizontal = 12.dp)
    ) {
        val density = LocalDensity.current
        val decay = rememberSplineBasedDecay<Float>()

        val buttonSize = 52.dp
        val travelPx = with(density) { (maxWidth - buttonSize).coerceAtLeast(0.dp).toPx() }

        val state = remember {
            AnchoredDraggableState(
                initialValue = Anchor.Right,
                positionalThreshold = { distance -> distance * 0.5f },
                velocityThreshold = { with(density) { 125.dp.toPx() } },
                snapAnimationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                decayAnimationSpec = decay
            )
        }

        LaunchedEffect(travelPx) {
            val anchors = DraggableAnchors {
                Anchor.Left at 0f
                Anchor.Right at travelPx
            }
            state.updateAnchors(anchors)

            // If offset wasn't initialized yet, start at RIGHT
            if (state.offset.isNaN()) state.snapTo(Anchor.Right)
        }

        val x = if (state.offset.isNaN()) travelPx else state.requireOffset()

        IconButton(
            onClick = {},
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset { IntOffset(x.roundToInt(), 0) }
                .anchoredDraggable(
                    state = state,
                    orientation = Orientation.Horizontal
                )
        ) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Drag")
        }
    }
}


@Composable
fun VerticalBouncingAnimation() {
    val infiniteTransition = rememberInfiniteTransition()
//1
    // start and end color for icon
    val startColor = Color.Green
    val endColor = Color.Black
//2
    val animatedColor by infiniteTransition.animateColor(
        initialValue = startColor,
        targetValue = endColor,
        animationSpec = infiniteRepeatable(
            tween(800, easing = FastOutLinearInEasing),
            RepeatMode.Reverse,
        )
    )
//3
    val position by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse)
    )
//4
    Icon(
        Icons.Default.Favorite,
        tint = animatedColor,
        contentDescription = "Heart Icon",
        modifier = Modifier
            .size(50.dp)
            .offset(y = position.dp)
    )
}
