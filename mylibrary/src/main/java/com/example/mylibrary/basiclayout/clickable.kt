package com.example.mylibrary.basiclayout

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import kotlin.random.Random

fun Modifier.bouncingClickable(
    enabled: Boolean = true,
    onClick: () -> Unit
) = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animationTransition = updateTransition(isPressed, label = "BouncingClickableTransition")
    val scaleFactor by animationTransition.animateFloat(
        targetValueByState = { pressed -> if (pressed) 1.1f else 1f },
        label = "BouncingClickableScaleFactorTransition",
       // transitionSpec = { tween(durationMillis = 500) }
    )
    val opacity by animationTransition.animateFloat(
        targetValueByState = { pressed -> if (pressed) 0.7f else 1f },
        label = "BouncingClickableOpacityTransition"
    )

    this
        .graphicsLayer {
            this.scaleX = scaleFactor
            this.scaleY = scaleFactor
            this.alpha = opacity
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled

        ) {
            onClick()
        }

}


fun Modifier.drawDashedLine(color: Color=Color.Black) = composed {
    drawBehind {
        drawLine(
            color = color,
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = 2f
        )
    }
}


@Composable
fun Modifier.scrollbar(
    state: LazyListState,
    horizontal: Boolean,
    alignEnd: Boolean = true,
    thickness: Dp = 4.dp,
    fixedKnobRatio: Float? = null,
    knobCornerRadius: Dp = 4.dp,
    trackCornerRadius: Dp = 2.dp,
    knobColor: Color = Color.Black,
    trackColor: Color = Color.White,
    padding: Dp = 0.dp,
    visibleAlpha: Float = 1f,
    hiddenAlpha: Float = 0f,
    fadeInAnimationDurationMs: Int = 150,
    fadeOutAnimationDurationMs: Int = 500,
    fadeOutAnimationDelayMs: Int = 1000,
): Modifier {
    check(thickness > 0.dp) { "Thickness must be a positive integer." }
    check(fixedKnobRatio == null || fixedKnobRatio < 1f) {
        "A fixed knob ratio must be smaller than 1."
    }
    check(knobCornerRadius >= 0.dp) { "Knob corner radius must be greater than or equal to 0." }
    check(trackCornerRadius >= 0.dp) { "Track corner radius must be greater than or equal to 0." }
    check(hiddenAlpha <= visibleAlpha) { "Hidden alpha cannot be greater than visible alpha." }
    check(fadeInAnimationDurationMs >= 0) {
        "Fade in animation duration must be greater than or equal to 0."
    }
    check(fadeOutAnimationDurationMs >= 0) {
        "Fade out animation duration must be greater than or equal to 0."
    }
    check(fadeOutAnimationDelayMs >= 0) {
        "Fade out animation delay must be greater than or equal to 0."
    }

    val targetAlpha =
        if (state.isScrollInProgress) {
            visibleAlpha
        } else {
            hiddenAlpha
        }
    val animationDurationMs =
        if (state.isScrollInProgress) {
            fadeInAnimationDurationMs
        } else {
            fadeOutAnimationDurationMs
        }
    val animationDelayMs =
        if (state.isScrollInProgress) {
            0
        } else {
            fadeOutAnimationDelayMs
        }

    val alpha by
    animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec =
        tween(delayMillis = animationDelayMs, durationMillis = animationDurationMs))

    return drawWithContent {
        drawContent()

        state.layoutInfo.visibleItemsInfo.firstOrNull()?.let { firstVisibleItem ->
            if (state.isScrollInProgress || alpha > 0f) {
                // Size of the viewport, the entire size of the scrollable composable we are decorating with
                // this scrollbar.
                val viewportSize =
                    if (horizontal) {
                        size.width
                    } else {
                        size.height
                    } - padding.toPx() * 2

                // The size of the first visible item. We use this to estimate how many items can fit in the
                // viewport. Of course, this works perfectly when all items have the same size. When they
                // don't, the scrollbar knob size will grow and shrink as we scroll.
                val firstItemSize = firstVisibleItem.size

                // The *estimated* size of the entire scrollable composable, as if it's all on screen at
                // once. It is estimated because it's possible that the size of the first visible item does
                // not represent the size of other items. This will cause the scrollbar knob size to grow
                // and shrink as we scroll, if the item sizes are not uniform.
                val estimatedFullListSize = firstItemSize * state.layoutInfo.totalItemsCount

                // The difference in position between the first pixels visible in our viewport as we scroll
                // and the top of the fully-populated scrollable composable, if it were to show all the
                // items at once. At first, the value is 0 since we start all the way to the top (or start
                // edge). As we scroll down (or towards the end), this number will grow.
                val viewportOffsetInFullListSpace =
                    state.firstVisibleItemIndex * firstItemSize + state.firstVisibleItemScrollOffset

                // Where we should render the knob in our composable.
                val knobPosition =
                    (viewportSize / estimatedFullListSize) * viewportOffsetInFullListSpace + padding.toPx()
                // How large should the knob be.
                val knobSize =
                    fixedKnobRatio?.let { it * viewportSize }
                        ?: (viewportSize * viewportSize) / estimatedFullListSize

                // Draw the track
                drawRoundRect(
                    color = trackColor,
                    topLeft =
                    when {
                        // When the scrollbar is horizontal and aligned to the bottom:
                        horizontal && alignEnd -> Offset(padding.toPx(), size.height - thickness.toPx())
                        // When the scrollbar is horizontal and aligned to the top:
                        horizontal && !alignEnd -> Offset(padding.toPx(), 0f)
                        // When the scrollbar is vertical and aligned to the end:
                        alignEnd -> Offset(size.width - thickness.toPx(), padding.toPx())
                        // When the scrollbar is vertical and aligned to the start:
                        else -> Offset(0f, padding.toPx())
                    },
                    size =
                    if (horizontal) {
                        Size(size.width - padding.toPx() * 2, thickness.toPx())
                    } else {
                        Size(thickness.toPx(), size.height - padding.toPx() * 2)
                    },
                    alpha = alpha,
                    cornerRadius = CornerRadius(x = trackCornerRadius.toPx(), y = trackCornerRadius.toPx()),
                )

                // Draw the knob
                drawRoundRect(
                    color = knobColor,
                    topLeft =
                    when {
                        // When the scrollbar is horizontal and aligned to the bottom:
                        horizontal && alignEnd -> Offset(knobPosition, size.height - thickness.toPx())
                        // When the scrollbar is horizontal and aligned to the top:
                        horizontal && !alignEnd -> Offset(knobPosition, 0f)
                        // When the scrollbar is vertical and aligned to the end:
                        alignEnd -> Offset(size.width - thickness.toPx(), knobPosition)
                        // When the scrollbar is vertical and aligned to the start:
                        else -> Offset(0f, knobPosition)
                    },
                    size =
                    if (horizontal) {
                        Size(knobSize, thickness.toPx())
                    } else {
                        Size(thickness.toPx(), knobSize)
                    },
                    alpha = alpha,
                    cornerRadius = CornerRadius(x = knobCornerRadius.toPx(), y = knobCornerRadius.toPx()),
                )
            }
        }
    }
}


@Composable
fun PaperBackground(
    modifier: Modifier = Modifier,
    paperColor: Color = Color(0xFFFAF0E6),
    textureColor: Color = Color(0xFF8B7D6B),
    noiseIntensity: Float = 0.3f
) {
    val textureBitmap = createNoisyTexture(paperColor, textureColor, noiseIntensity)

    Image(
        bitmap = textureBitmap,
        contentDescription = "Paper texture",
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )
}

private fun createNoisyTexture(
    paperColor: Color,
    textureColor: Color,
    noiseIntensity: Float,
    width: Int = 256,
    height: Int = 256
): ImageBitmap {
    val random = Random(System.currentTimeMillis())
    val bitmap = createBitmap(width, height)

    for (x in 0 until width) {
        for (y in 0 until height) {
            val noise = random.nextFloat() * noiseIntensity
            val r = (paperColor.red + (textureColor.red - paperColor.red) * noise).coerceIn(0f, 1f)
            val g = (paperColor.green + (textureColor.green - paperColor.green) * noise).coerceIn(0f, 1f)
            val b = (paperColor.blue + (textureColor.blue - paperColor.blue) * noise).coerceIn(0f, 1f)
            val color = Color(r, g, b)
            bitmap.setPixel(x, y, color.toArgb())
        }
    }

    return bitmap.asImageBitmap()
}
