package org.skobinsky.rippler.node.drawBasedRipple

import androidx.collection.mutableObjectListOf
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.LayoutAwareModifierNode
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.requireDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Abstract [Modifier.Node] that provides common functionality used by ripple node implementations.
 * Implementing classes should use [stateLayer] to draw the [StateLayer], so they only need to
 * handle showing the ripple effect when pressed, and not other [Interaction]s.
 */
internal abstract class RippleNode(
    private val interactionSource: InteractionSource,
    protected val bounded: Boolean,
    private val radius: Dp,
    private val color: ColorProducer,
    protected val rippleAlpha: () -> RippleAlpha,
    private val rippleDrawCommand: RippleDrawCommand = SmoothRippleCommand
) :
    Modifier.Node(),
    CompositionLocalConsumerModifierNode,
    DrawModifierNode,
    LayoutAwareModifierNode {
    final override val shouldAutoInvalidate: Boolean = false

    private var stateLayer: StateLayer? = null

    // The following are calculated inside onRemeasured(). These must be initialized before adding
    // a ripple.

    protected var targetRadius: Float = 0f

    // The size is needed for Android to update ripple bounds if the size changes
    protected var rippleSize: Size = Size.Companion.Zero
        private set

    val rippleColor: Color
        get() = color()

    // Track interactions that were emitted before we have been placed - we need to wait until we
    // have a valid size in order to set the radius and size correctly.
    private var hasValidSize = false
    private val pendingInteractions = mutableObjectListOf<PressInteraction>()

    override fun onRemeasured(size: IntSize) {
        hasValidSize = true
        val density = requireDensity()
        rippleSize = size.toSize()
        targetRadius =
            with(density) {
                if (radius.isUnspecified) {
                    // Explicitly calculate the radius instead of using RippleDrawable.RADIUS_AUTO
                    // on
                    // Android since the latest spec does not match with the existing radius
                    // calculation
                    // in the framework.
                    getRippleEndRadius(bounded, rippleSize)
                } else {
                    radius.toPx()
                }
            }
        // Flush any pending interactions that were waiting for measurement
        pendingInteractions.forEach { handlePressInteraction(it) }
        pendingInteractions.clear()
    }

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction -> {
                        if (hasValidSize) {
                            handlePressInteraction(interaction)
                        } else {
                            // Handle these later when we have a valid size
                            pendingInteractions += interaction
                        }
                    }

                    else -> updateStateLayer(interaction, this)
                }
            }
        }
    }

    private fun handlePressInteraction(pressInteraction: PressInteraction) {
        when (pressInteraction) {
            is PressInteraction.Press -> addRipple(
                pressInteraction,
                rippleSize,
                targetRadius
            )

            is PressInteraction.Release -> removeRipple(pressInteraction.press)
            is PressInteraction.Cancel -> removeRipple(pressInteraction.press)
        }
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        stateLayer?.run { drawStateLayer(targetRadius, rippleColor, rippleDrawCommand) }
        drawRipples()
    }

    abstract fun DrawScope.drawRipples()

    abstract fun addRipple(
        interaction: PressInteraction.Press,
        size: Size,
        targetRadius: Float
    )

    abstract fun removeRipple(interaction: PressInteraction.Press)

    private fun updateStateLayer(
        interaction: Interaction,
        scope: CoroutineScope
    ) {
        val stateLayer =
            stateLayer
                ?: StateLayer(bounded, rippleAlpha).also { instance ->
                    // Invalidate when adding the state layer so we can start drawing it
                    invalidateDraw()
                    stateLayer = instance
                }
        stateLayer.handleInteraction(interaction, scope)
    }
}