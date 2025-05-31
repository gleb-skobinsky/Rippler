package org.skobinsky.rippler.node

import androidx.annotation.FloatRange
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.unit.Dp
import org.skobinsky.rippler.node.drawBasedRipple.CircularRippleCommand
import org.skobinsky.rippler.node.drawBasedRipple.RippleAnimationDuration
import org.skobinsky.rippler.node.drawBasedRipple.RippleNodeFactory

@Stable
fun circularRipple(
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    @FloatRange(0.0, 1.0)
    startRadiusFraction: Float = DefaultStartRadiusFraction,
    animations: RippleAnimationDuration = RippleAnimationDuration.Default,
    color: Color = Color.Unspecified
): IndicationNodeFactory {
    return when {
        radius != Dp.Unspecified ||
                color != Color.Unspecified ||
                startRadiusFraction != DefaultStartRadiusFraction ||
                animations != RippleAnimationDuration.Default -> {
            RippleNodeFactory(
                bounded = bounded,
                radius = radius,
                color = color,
                animations = animations,
                startRadiusFraction = startRadiusFraction,
                drawCommand = CircularRippleCommand
            )
        }

        bounded -> DefaultCircBoundedRipple
        else -> DefaultCircUnboundedRipple
    }
}

@Stable
fun circularRipple(
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    @FloatRange(0.0, 1.0)
    startRadiusFraction: Float = DefaultStartRadiusFraction,
    animations: RippleAnimationDuration = RippleAnimationDuration.Default,
    color: ColorProducer
): IndicationNodeFactory {
    return RippleNodeFactory(
        bounded = bounded,
        radius = radius,
        colorProducer = color,
        animations = animations,
        startRadiusFraction = startRadiusFraction,
        drawCommand = CircularRippleCommand
    )
}

private val DefaultCircBoundedRipple = RippleNodeFactory(
    bounded = true,
    radius = Dp.Unspecified,
    color = Color.Unspecified,
    startRadiusFraction = DefaultStartRadiusFraction,
    drawCommand = CircularRippleCommand,
    animations = RippleAnimationDuration.Default
)
private val DefaultCircUnboundedRipple = RippleNodeFactory(
    bounded = false,
    radius = Dp.Unspecified,
    color = Color.Unspecified,
    startRadiusFraction = DefaultStartRadiusFraction,
    drawCommand = CircularRippleCommand,
    animations = RippleAnimationDuration.Default
)