package org.skobinsky.rippler.node.drawBasedRipple

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.max

/**
 * [RippleAnimation]s are drawn as part of [Ripple] as a visual indicator for an different
 * [androidx.compose.foundation.interaction.Interaction]s.
 *
 * Use [androidx.compose.foundation.clickable] or [androidx.compose.foundation.indication] to add a
 * ripple to your component, which contains a RippleAnimation for pressed states, and a state layer
 * for other states.
 *
 * This is a default implementation based on the Material Design specification.
 *
 * Draws a circular ripple effect with an origin starting at the input touch point and with a radius
 * expanding from 60% of the final value. The ripple origin animates to the center of its target
 * layout for the bounded version and stays in the center for the unbounded one.
 *
 * @param origin The position the animation will start from. If null the animation will start from
 *   the center of the layout bounds.
 * @param radius Effects grow up to this size.
 * @param bounded If true the effect should be clipped by the target layout bounds.
 */
internal class RippleAnimation(
    private var origin: Offset?,
    private val radius: Float,
    private val startRadiusFraction: Float,
    private val bounded: Boolean,
    private val onDraw: RippleDrawCommand = SmoothRippleCommand,
    private val animations: RippleAnimationDuration
) {
    private var startRadius: Float? = null

    private var targetCenter: Offset? = null

    private val animatedAlpha = Animatable(0f)
    private val animatedRadiusPercent = Animatable(0f)
    private val animatedCenterPercent = Animatable(0f)

    private val finishSignalDeferred = CompletableDeferred<Unit>(null)

    private var finishedFadingIn by mutableStateOf(false)
    private var finishRequested by mutableStateOf(false)

    suspend fun animate() {
        fadeIn()
        finishedFadingIn = true
        finishSignalDeferred.await()
        fadeOut()
    }

    private suspend fun fadeIn() {
        coroutineScope {
            launch {
                animatedAlpha.animateTo(
                    1f,
                    tween(
                        durationMillis = animations.fadeInDuration,
                        easing = LinearEasing
                    )
                )
            }
            launch {
                animatedRadiusPercent.animateTo(
                    1f,
                    tween(
                        durationMillis = animations.radiusDuration,
                        easing = FastOutSlowInEasing
                    )
                )
            }
            launch {
                animatedCenterPercent.animateTo(
                    1f,
                    tween(
                        durationMillis = animations.radiusDuration,
                        easing = LinearEasing
                    )
                )
            }
        }
    }

    private suspend fun fadeOut() {
        coroutineScope {
            launch {
                animatedAlpha.animateTo(
                    0f,
                    tween(
                        durationMillis = animations.fadeOutDuration,
                        easing = LinearEasing
                    )
                )
            }
        }
    }

    fun finish() {
        finishRequested = true
        finishSignalDeferred.complete(Unit)
    }

    fun DrawScope.draw(color: Color) {
        if (startRadius == null) {
            startRadius = getRippleStartRadius(size, startRadiusFraction)
        }
        if (origin == null) {
            origin = center
        }
        if (targetCenter == null) {
            targetCenter = Offset(size.width / 2.0f, size.height / 2.0f)
        }

        val alpha =
            if (finishRequested && !finishedFadingIn) {
                // If we are still fading-in we should immediately switch to the final alpha.
                1f
            } else {
                animatedAlpha.value
            }

        val radius = lerp(
            start = startRadius!!,
            stop = radius,
            fraction = animatedRadiusPercent.value
        )
        val centerOffset = Offset(
            lerp(origin!!.x, targetCenter!!.x, animatedCenterPercent.value),
            lerp(origin!!.y, targetCenter!!.y, animatedCenterPercent.value),
        )
        val modulatedColor = color.copy(alpha = color.alpha * alpha)
        if (bounded) {
            clipRect {
                this@draw.onDraw(modulatedColor, centerOffset, radius)
            }
        } else {
            this@draw.onDraw(modulatedColor, centerOffset, radius)
        }
    }
}

/**
 * According to specs the starting radius is equal to 60% of the largest dimension of the surface it
 * belongs to.
 */
internal fun getRippleStartRadius(size: Size, fraction: Float) =
    max(size.width, size.height) * fraction

/**
 * According to specs the ending radius
 * - expands to 10dp beyond the border of the surface it belongs to for bounded ripples
 * - fits within the border of the surface it belongs to for unbounded ripples
 */
internal fun Density.getRippleEndRadius(bounded: Boolean, size: Size): Float {
    val radiusCoveringBounds =
        (Offset(size.width, size.height).getDistance() / 2f)
    return if (bounded) {
        radiusCoveringBounds + BoundedRippleExtraRadius.toPx()
    } else {
        radiusCoveringBounds
    }
}

private val BoundedRippleExtraRadius = 10.dp

@Immutable
data class RippleAnimationDuration(
    val fadeInDuration: Int = 75,
    val radiusDuration: Int = 225,
    val fadeOutDuration: Int = 150
) {
    companion object {
        val Default = RippleAnimationDuration()
    }

    operator fun times(count: Int): RippleAnimationDuration {
        return RippleAnimationDuration(
            fadeInDuration = fadeInDuration * count,
            radiusDuration = radiusDuration * count,
            fadeOutDuration = fadeOutDuration * count
        )
    }
}