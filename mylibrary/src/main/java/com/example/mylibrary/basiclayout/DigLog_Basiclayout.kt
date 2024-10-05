package com.example.mylibrary.basiclayout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mylibrary.basiclayout.Column_Basiclayout
import kotlinx.coroutines.delay

@Composable
fun DigLog_Basiclayout(
    state: MutableState<Boolean>,
    title: String = "标题",
    width:Dp = 600.dp,
    height:Dp = 400.dp,
    content: @Composable () -> Unit
) {
    val alphas by animateFloatAsState(if (state.value) .6f else 0f, animationSpec =
    if (state.value) tween(600) else tween(300))
    var isshow by remember { mutableStateOf(false) }
    LaunchedEffect(state.value) {
        if (state.value) {
            isshow = true
        } else {
            delay(300)
            isshow = false
        }
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (isshow) {
            Spacer(modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = alphas
                }
                .background(Color.Black)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { })
        }

        AnimatedVisibility(
            state.value,
            enter = scaleIn(),
            exit = scaleOut()
        ) {
            Column_Basiclayout(
                modifier = Modifier
                    .width(width)
                    .height(height)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
                    .background(
                        MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {

                    content()
            }
        }

    }
}