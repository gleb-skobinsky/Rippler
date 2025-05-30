package org.skobinsky.rippler.node.opacityRipple

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.runtime.Stable

@Stable
fun opacityRipple(
    fadeInDuration: Int,
    fadeOutDuration: Int,
    minAlpha: Float = 0.4f
): IndicationNodeFactory {
    return OpacityRippleFactory(fadeInDuration, fadeOutDuration, minAlpha)
}

@Stable
fun opacityRipple(
    fadeInSpec: FiniteAnimationSpec<Float>,
    fadeOutSpec: FiniteAnimationSpec<Float>,
    minAlpha: Float = 0.4f
): IndicationNodeFactory {
    return OpacityRippleFactory(fadeInSpec, fadeOutSpec, minAlpha)
}