package org.skobinsky.rippler.node.drawBasedRipple

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.RippleDefaults
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.observeReads
import androidx.compose.ui.unit.Dp

internal class DelegatingRippleNode(
    private val interactionSource: InteractionSource,
    private val bounded: Boolean,
    private val radius: Dp,
    private val startRadiusFraction: Float,
    color: ColorProducer,
    drawCommand: RippleDrawCommand
) : AbstractDelegatingRippleNode<DelegatableNode>(
    color = color,
    nodeProducer = { color, alpha ->
        createUniversalRippleNode(
            interactionSource = interactionSource,
            bounded = bounded,
            radius = radius,
            color = color,
            rippleAlpha = alpha,
            drawCommand = drawCommand,
            startRadiusFraction = startRadiusFraction
        )
    }
)

internal open class AbstractDelegatingRippleNode<Node : DelegatableNode>(
    private val color: ColorProducer,
    private val nodeProducer: ColoredRippleNodeProducer<Node>
) : DelegatingNode(), CompositionLocalConsumerModifierNode,
    ObserverModifierNode {
    private var rippleNode: Node? = null

    override fun onAttach() {
        updateConfiguration()
    }

    override fun onObservedReadsChanged() {
        updateConfiguration()
    }

    /**
     * Handles [LocalRippleConfiguration] changing between null / non-null. Changes to
     * [RippleConfiguration.color] and [RippleConfiguration.rippleAlpha] are handled as part of the
     * ripple definition.
     */
    private fun updateConfiguration() {
        observeReads {
            val configuration = currentValueOf(LocalRippleConfig)
            if (configuration == null) {
                removeRipple()
            } else {
                if (rippleNode == null) attachNewRipple()
            }
        }
    }

    private fun attachNewRipple() {
        val calculateColor = ColorProducer {
            val userColor = color()
            if (userColor.isSpecified) {
                userColor
            } else {
                // If this is null, the ripple will be removed, so this should always be non-null in
                // normal use
                val rippleConfiguration =
                    currentValueOf(LocalRippleConfig)
                if (rippleConfiguration?.color?.isSpecified == true) {
                    rippleConfiguration.color
                } else {
                    currentValueOf(LocalContentColor)
                }
            }
        }

        val calculateRippleAlpha = {
            // If this is null, the ripple will be removed, so this should always be non-null in
            // normal use
            val rippleConfiguration = currentValueOf(LocalRippleConfig)
            rippleConfiguration?.rippleAlpha ?: RippleDefaults.RippleAlpha
        }

        rippleNode = delegate(
            nodeProducer.create(
                calculateColor,
                calculateRippleAlpha
            )
        )
    }

    private fun removeRipple() {
        rippleNode?.let { undelegate(it) }
    }
}
