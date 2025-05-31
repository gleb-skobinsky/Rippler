package org.skobinsky.rippler.node

import androidx.annotation.FloatRange
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.unit.Dp
import org.skobinsky.rippler.node.drawBasedRipple.RippleAnimationDuration
import org.skobinsky.rippler.node.drawBasedRipple.RippleDrawCommand
import org.skobinsky.rippler.node.drawBasedRipple.RippleNodeFactory
import org.skobinsky.rippler.node.experimental.ExperimentalRippleApi

internal const val DefaultStartRadiusFraction = 0.3f

@Composable
@ExperimentalRippleApi
fun rememberCustomRipple(
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    color: Color = Color.Unspecified,
    @FloatRange(0.0, 1.0)
    startRadiusFraction: Float = DefaultStartRadiusFraction,
    animations: RippleAnimationDuration = RippleAnimationDuration.Default,
    drawCommand: RippleDrawCommand
): IndicationNodeFactory {
    val rememberedCommand = remember { drawCommand }
    return remember(bounded, radius, color, rememberedCommand, animations) {
        RippleNodeFactory(
            bounded = bounded,
            radius = radius,
            startRadiusFraction = startRadiusFraction,
            color = color,
            drawCommand = rememberedCommand,
            animations = animations
        )
    }
}

@Composable
@ExperimentalRippleApi
fun rememberCustomRipple(
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    @FloatRange(0.0, 1.0)
    startRadiusFraction: Float = DefaultStartRadiusFraction,
    animations: RippleAnimationDuration = RippleAnimationDuration.Default,
    color: ColorProducer,
    drawCommand: RippleDrawCommand
): IndicationNodeFactory {
    val rememberedCommand = remember { drawCommand }
    return remember(bounded, radius, color, rememberedCommand) {
        RippleNodeFactory(
            bounded = bounded,
            radius = radius,
            colorProducer = color,
            startRadiusFraction = startRadiusFraction,
            drawCommand = rememberedCommand,
            animations = animations
        )
    }
}
