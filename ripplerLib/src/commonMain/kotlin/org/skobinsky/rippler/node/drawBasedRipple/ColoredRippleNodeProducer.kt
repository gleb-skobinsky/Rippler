package org.skobinsky.rippler.node.drawBasedRipple

import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.node.DelegatableNode

fun interface ColoredRippleNodeProducer<Node : DelegatableNode> {
    fun create(
        color: ColorProducer,
        alpha: () -> RippleAlpha
    ): Node
}