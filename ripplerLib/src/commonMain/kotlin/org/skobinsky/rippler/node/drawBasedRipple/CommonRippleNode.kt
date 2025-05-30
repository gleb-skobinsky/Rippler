package org.skobinsky.rippler.node.drawBasedRipple

import androidx.collection.MutableScatterMap
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.launch

internal class CommonRippleNode(
    interactionSource: InteractionSource,
    bounded: Boolean,
    radius: Dp,
    private val startRadiusFraction: Float,
    color: ColorProducer,
    rippleAlpha: () -> RippleAlpha,
    private val drawCommand: RippleDrawCommand
) : RippleNode(
    interactionSource = interactionSource,
    bounded = bounded,
    radius = radius,
    color = color,
    rippleAlpha = rippleAlpha,
    rippleDrawCommand = drawCommand
) {
    private val ripples =
        MutableScatterMap<PressInteraction.Press, RippleAnimation>()

    override fun addRipple(
        interaction: PressInteraction.Press,
        size: Size,
        targetRadius: Float
    ) {
        // Finish existing ripples
        ripples.forEach { _, ripple -> ripple.finish() }
        val origin = if (bounded) {
            interaction.pressPosition
        } else {
            null
        }
        val rippleAnimation = RippleAnimation(
            origin = origin,
            radius = targetRadius,
            bounded = bounded,
            onDraw = drawCommand,
            startRadiusFraction = startRadiusFraction
        )
        ripples[interaction] = rippleAnimation
        coroutineScope.launch {
            try {
                rippleAnimation.animate()
            } finally {
                ripples.remove(interaction)
                invalidateDraw()
            }
        }
        invalidateDraw()
    }

    override fun removeRipple(interaction: PressInteraction.Press) {
        ripples[interaction]?.finish()
    }

    override fun DrawScope.drawRipples() {
        val alpha = rippleAlpha().pressedAlpha
        if (alpha != 0f) {
            ripples.forEach { _, ripple ->
                with(ripple) {
                    draw(
                        rippleColor.copy(
                            alpha = alpha
                        )
                    )
                }
            }
        }
    }

    override fun onDetach() {
        ripples.clear()
    }
}