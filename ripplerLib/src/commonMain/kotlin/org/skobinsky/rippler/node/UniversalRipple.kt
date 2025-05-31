package org.skobinsky.rippler.node

import androidx.annotation.FloatRange
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.unit.Dp
import org.skobinsky.rippler.node.drawBasedRipple.RippleAnimationDuration
import org.skobinsky.rippler.node.drawBasedRipple.RippleNodeFactory

private const val DefaultSoftRadius = 0.15f

@Stable
fun universalRipple(
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    @FloatRange(0.0, 1.0)
    startRadiusFraction: Float = DefaultSoftRadius,
    color: Color = Color.Unspecified,
    animations: RippleAnimationDuration = RippleAnimationDuration.Default
): IndicationNodeFactory {
    return when {
        radius != Dp.Unspecified ||
                color != Color.Unspecified ||
                startRadiusFraction != DefaultSoftRadius ||
                animations != RippleAnimationDuration.Default -> {
            RippleNodeFactory(
                bounded = bounded,
                radius = radius,
                color = color,
                startRadiusFraction = startRadiusFraction,
                animations = animations
            )
        }

        bounded -> DefaultBoundedRipple
        else -> DefaultUnboundedRipple
    }
}

@Stable
fun universalRipple(
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    @FloatRange(0.0, 1.0)
    startRadiusFraction: Float = DefaultSoftRadius,
    animations: RippleAnimationDuration = RippleAnimationDuration.Default,
    color: ColorProducer
): IndicationNodeFactory {
    return RippleNodeFactory(
        bounded = bounded,
        radius = radius,
        colorProducer = color,
        startRadiusFraction = startRadiusFraction,
        animations = animations
    )
}


private val DefaultBoundedRipple = RippleNodeFactory(
    bounded = true,
    radius = Dp.Unspecified,
    color = Color.Unspecified,
    startRadiusFraction = DefaultSoftRadius,
    animations = RippleAnimationDuration.Default
)
private val DefaultUnboundedRipple = RippleNodeFactory(
    bounded = false,
    radius = Dp.Unspecified,
    color = Color.Unspecified,
    startRadiusFraction = DefaultSoftRadius,
    animations = RippleAnimationDuration.Default
)