package org.skobinsky.rippler.node.opacityRipple

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.runtime.Stable
import androidx.compose.ui.node.DelegatableNode

@Stable
internal class OpacityRippleFactory(
    private val fadeInSpec: FiniteAnimationSpec<Float>,
    private val fadeOutSpec: FiniteAnimationSpec<Float>,
    private val minAlpha: Float,
) : IndicationNodeFactory {
    constructor(
        fadeInDuration: Int,
        fadeOutDuration: Int,
        minAlpha: Float,
    ) : this(
        tween(fadeInDuration),
        tween(fadeOutDuration),
        minAlpha
    )

    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return OpacityRippleNode(
            fadeInSpec = fadeInSpec,
            fadeOutSpec = fadeOutSpec,
            minAlpha = minAlpha,
            interactionSource = interactionSource
        )
    }

    override fun hashCode(): Int {
        var result = fadeInSpec.hashCode()
        result = 31 * result + fadeOutSpec.hashCode()
        result = 31 * result + minAlpha.hashCode()
        return result
    }

    // While FiniteAnimationSpec interface itself is not stable or supports
    // structural equality, its widespread children like TweenSpec or SpringSpec
    // do support structural equality, so they can be safely compared here.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OpacityRippleFactory) return false
        if (fadeInSpec != other.fadeInSpec) return false
        if (minAlpha != other.minAlpha) return false
        return fadeOutSpec == other.fadeOutSpec
    }

}