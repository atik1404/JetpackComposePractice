package com.jetpack.compose.screens.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

enum class VoiceRecordState { Idle, Recording, Locked }


@Composable
fun WhatsAppVoiceRecordingUi(
    modifier: Modifier = Modifier,
    cancelThreshold: Dp = 90.dp,
    lockThreshold: Dp = 90.dp,
    onStartRecording: () -> Unit,
    onCancelRecording: () -> Unit,
    onStopAndSend: () -> Unit,
    onLocked: () -> Unit,
    onStopLocked: () -> Unit, // call when user presses stop in locked mode
) {
    var state by remember { mutableStateOf(VoiceRecordState.Idle) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var elapsedMs by remember { mutableStateOf(0L) }

    val density = LocalDensity.current
    val cancelPx = with(density) { cancelThreshold.toPx() }
    val lockPx = with(density) { lockThreshold.toPx() }

    // timer
    LaunchedEffect(state) {
        if (state == VoiceRecordState.Idle) {
            elapsedMs = 0L
            return@LaunchedEffect
        }
        while (state == VoiceRecordState.Recording || state == VoiceRecordState.Locked) {
            delay(1000)
            elapsedMs += 1000
        }
    }

    val cancelProgress = ((-dragOffset.x) / cancelPx).coerceIn(0f, 1f) // drag left => negative x
    val lockProgress = ((-dragOffset.y) / lockPx).coerceIn(0f, 1f)     // drag up => negative y

    Box(modifier = modifier.fillMaxWidth()) {

        // === Recording bar (like your screenshot) ===
        AnimatedVisibility(
            visible = state != VoiceRecordState.Idle,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 12.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // left mic icon (red-ish effect by lowering alpha on surface)
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(Modifier.width(10.dp))

                Text(
                    text = formatElapsed(elapsedMs),
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.width(14.dp))

                // “slide to cancel <” fades out as you approach cancel threshold
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = 1f - cancelProgress
                            translationX = dragOffset.x * 0.35f // subtle slide
                        }
                ) {
                    Text(
                        text = "slide to cancel",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                    )
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                    )
                }

                Spacer(Modifier.weight(1f))
            }
        }

        // === Right “lock pillar” (like WhatsApp) ===
        if (state != VoiceRecordState.Idle) {
            LockPillar(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp),
                locked = state == VoiceRecordState.Locked,
                lockProgress = lockProgress,
                onStopLocked = {
                    // stop button behavior in locked mode
                    state = VoiceRecordState.Idle
                    dragOffset = Offset.Zero
                    onStopLocked()
                }
            )
        }

        // === The actual mic hold/drag target ===
        // Keep it aligned to the bottom of the pillar (WhatsApp feel)
        MicHoldGestureTarget(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 22.dp) // put it inside pillar
                .size(54.dp),
            enabled = true,
            cancelPx = cancelPx,
            lockPx = lockPx,
            onDrag = { dragOffset = it },
            onStart = {
                dragOffset = Offset.Zero
                state = VoiceRecordState.Recording
                onStartRecording()
            },
            onCancel = {
                state = VoiceRecordState.Idle
                dragOffset = Offset.Zero
                onCancelRecording()
            },
            onLock = {
                state = VoiceRecordState.Locked
                dragOffset = Offset.Zero
                onLocked()
            },
            onStop = {
                // finger up while recording (not locked) => stop & send
                state = VoiceRecordState.Idle
                dragOffset = Offset.Zero
                onStopAndSend()
            }
        )
    }
}

@Composable
private fun LockPillar(
    modifier: Modifier = Modifier,
    locked: Boolean,
    lockProgress: Float,
    onStopLocked: () -> Unit,
) {
    // simple “pill”
    Column(
        modifier = modifier
            .width(58.dp)
            .height(150.dp)
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.22f),
                shape = RoundedCornerShape(26.dp)
            )
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // lock top
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = if (locked) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
            modifier = Modifier
                .size(26.dp)
                .graphicsLayer {
                    // slightly scale up as you drag up
                    val s = 1f + (0.15f * lockProgress)
                    scaleX = s
                    scaleY = s
                }
        )

        // arrow hint only while not locked
        if (!locked) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(28.dp)
            )
        } else {
            // In locked mode you usually show stop/send UI somewhere.
            // Minimal: a “stop” hint by tapping the mic area could also be done.
            Text(
                text = "Locked",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Spacer(Modifier.height(2.dp))
    }
}

@Composable
private fun MicHoldGestureTarget(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    cancelPx: Float,
    lockPx: Float,
    onDrag: (Offset) -> Unit,
    onStart: () -> Unit,
    onCancel: () -> Unit,
    onLock: () -> Unit,
    onStop: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.001f), // invisible hit area
                shape = CircleShape
            )
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput

                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val start = down.position

                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onStart()

                    var locked = false
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.first()

                        val offset = change.position - start
                        onDrag(offset)

                        if (!locked) {
                            // cancel
                            if (offset.x <= -cancelPx) {
                                haptic.performHapticFeedback(HapticFeedbackType.Reject)
                                onCancel()
                                // wait finger up
                                while (event.changes.any { it.pressed }) awaitPointerEvent()
                                break
                            }
                            // lock
                            if (offset.y <= -lockPx) {
                                locked = true
                                haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                                onLock()
                            }
                        }

                        // finger up
                        if (!change.pressed) {
                            if (!locked) onStop()
                            break
                        }
                        change.consume()
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // mic icon (you can replace with your own button UI)
        Box(
            modifier = Modifier
                .size(54.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.25f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Hold to record",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

private fun formatElapsed(ms: Long): String {
    val totalSec = (ms / 1000).toInt()
    val m = totalSec / 60
    val s = totalSec % 60
    return "%d:%02d".format(m, s)
}
