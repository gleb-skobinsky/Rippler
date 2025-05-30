package org.skobinsky.rippler.node.drawBasedRipple

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/**
 * Represents the layer underneath the press ripple, that displays an overlay for states such as
 * [DragInteraction.Start].
 *
 * Typically, there should be both an 'incoming' and an 'outgoing' layer, so that when transitioning
 * between two states, the incoming of the new state, and the outgoing of the old state can be
 * displayed. However, because:
 *
 * a) the duration of these outgoing transitions are so short (mostly 15ms, which is less than 1
 * frame at 60fps), and hence are barely noticeable if they happen at the same time as an incoming
 * transition b) two layers cause a lot of extra work, and related performance concerns
 *
 * We skip managing two layers, and instead only show one layer. The details for the
 * [AnimationSpec]s used are as follows:
 *
 * No state -> a state = incoming transition for the new state A state -> a different state =
 * incoming transition for the new state A state -> no state = outgoing transition for the old state
 *
 * @see incomingStateLayerAnimationSpecFor
 * @see outgoingStateLayerAnimationSpecFor
 */
internal class StateLayer(
    private val bounded: Boolean,
    private val rippleAlpha: () -> RippleAlpha
) {
    private val animatedAlpha = Animatable(0f)

    private val interactions: MutableList<Interaction> = mutableListOf()
    private var currentInteraction: Interaction? = null

    internal fun handleInteraction(
        interaction: Interaction,
        scope: CoroutineScope
    ) {
        when (interaction) {
            is HoverInteraction.Enter -> {
                interactions.add(interaction)
            }

            is HoverInteraction.Exit -> {
                interactions.remove(interaction.enter)
            }

            is FocusInteraction.Focus -> {
                interactions.add(interaction)
            }

            is FocusInteraction.Unfocus -> {
                interactions.remove(interaction.focus)
            }

            is DragInteraction.Start -> {
                interactions.add(interaction)
            }

            is DragInteraction.Stop -> {
                interactions.remove(interaction.start)
            }

            is DragInteraction.Cancel -> {
                interactions.remove(interaction.start)
            }

            else -> return
        }

        // The most recent interaction is the one we want to show
        val newInteraction = interactions.lastOrNull()

        if (currentInteraction != newInteraction) {
            if (newInteraction != null) {
                val rippleAlpha = rippleAlpha()
                val targetAlpha =
                    when (interaction) {
                        is HoverInteraction.Enter -> rippleAlpha.hoveredAlpha
                        is FocusInteraction.Focus -> rippleAlpha.focusedAlpha
                        is DragInteraction.Start -> rippleAlpha.draggedAlpha
                        else -> 0f
                    }
                val incomingAnimationSpec =
                    incomingStateLayerAnimationSpecFor(newInteraction)

                scope.launch {
                    animatedAlpha.animateTo(
                        targetAlpha,
                        incomingAnimationSpec
                    )
                }
            } else {
                val outgoingAnimationSpec =
                    outgoingStateLayerAnimationSpecFor(currentInteraction)

                scope.launch {
                    animatedAlpha.animateTo(
                        0f,
                        outgoingAnimationSpec
                    )
                }
            }
            currentInteraction = newInteraction
        }
    }

    fun DrawScope.drawStateLayer(
        radius: Float,
        color: Color,
        rippleDrawCommand: RippleDrawCommand
    ) {
        val alpha = animatedAlpha.value

        if (alpha > 0f) {
            val modulatedColor = color.copy(alpha = alpha)

            if (bounded) {
                clipRect {
                    this@drawStateLayer.rippleDrawCommand(modulatedColor, center, radius)
                }
            } else {
                this@drawStateLayer.rippleDrawCommand(modulatedColor, center, radius)
            }
        }
    }
}

/**
 * @return the [AnimationSpec] used when transitioning to [interaction], either from a previous
 *   state, or no state.
 */
private fun incomingStateLayerAnimationSpecFor(interaction: Interaction): AnimationSpec<Float> {
    return when (interaction) {
        is HoverInteraction.Enter -> DefaultTweenSpec
        is FocusInteraction.Focus -> TweenSpec(
            durationMillis = 45,
            easing = LinearEasing
        )

        is DragInteraction.Start -> TweenSpec(
            durationMillis = 45,
            easing = LinearEasing
        )

        else -> DefaultTweenSpec
    }
}

/** @return the [AnimationSpec] used when transitioning away from [interaction], to no state. */
private fun outgoingStateLayerAnimationSpecFor(interaction: Interaction?): AnimationSpec<Float> {
    return when (interaction) {
        is HoverInteraction.Enter -> DefaultTweenSpec
        is FocusInteraction.Focus -> DefaultTweenSpec
        is DragInteraction.Start -> TweenSpec(
            durationMillis = 150,
            easing = LinearEasing
        )

        else -> DefaultTweenSpec
    }
}

/** Default / fallback [AnimationSpec]. */
private val DefaultTweenSpec =
    TweenSpec<Float>(durationMillis = 15, easing = LinearEasing)
