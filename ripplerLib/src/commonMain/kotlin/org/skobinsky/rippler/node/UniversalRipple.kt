package org.skobinsky.rippler.node

import androidx.annotation.FloatRange
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.unit.Dp
import org.skobinsky.rippler.node.drawBasedRipple.RippleNodeFactory

@Stable
fun universalRipple(
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    @FloatRange(0.0, 1.0)
    startRadiusFraction: Float = DefaultStartRadiusFraction,
    color: Color = Color.Unspecified
): IndicationNodeFactory {
    return when {
        radius != Dp.Unspecified || color != Color.Unspecified -> {
            RippleNodeFactory(
                bounded = bounded,
                radius = radius,
                color = color,
                startRadiusFraction = startRadiusFraction
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
    startRadiusFraction: Float = DefaultStartRadiusFraction,
    color: ColorProducer
): IndicationNodeFactory {
    return RippleNodeFactory(
        bounded = bounded,
        radius = radius,
        colorProducer = color,
        startRadiusFraction = startRadiusFraction
    )
}


private val DefaultBoundedRipple = RippleNodeFactory(
    bounded = true,
    radius = Dp.Unspecified,
    color = Color.Unspecified,
    startRadiusFraction = DefaultStartRadiusFraction
)
private val DefaultUnboundedRipple = RippleNodeFactory(
    bounded = false,
    radius = Dp.Unspecified,
    color = Color.Unspecified,
    startRadiusFraction = DefaultStartRadiusFraction
)