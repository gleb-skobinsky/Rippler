package org.violet.uiKit.ripple.node

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.unit.Dp
import org.skobinsky.rippler.node.drawBasedRipple.RippleDrawCommand
import org.skobinsky.rippler.node.drawBasedRipple.RippleNodeFactory
import org.skobinsky.rippler.node.experimental.ExperimentalRippleApi

@Stable
fun universalRipple(
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    color: Color = Color.Unspecified
): IndicationNodeFactory {
    return when {
        radius != Dp.Unspecified || color != Color.Unspecified -> {
            RippleNodeFactory(bounded, radius, color)
        }

        bounded -> DefaultBoundedRipple
        else -> DefaultUnboundedRipple
    }
}

@Stable
fun universalRipple(
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    color: ColorProducer
): IndicationNodeFactory {
    return RippleNodeFactory(bounded, radius, colorProducer = color)
}

@Composable
@ExperimentalRippleApi
fun rememberCustomRipple(
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    color: Color = Color.Unspecified,
    drawCommand: RippleDrawCommand
): IndicationNodeFactory {
    val rememberedCommand = remember { drawCommand }
    return remember(bounded, radius, color, rememberedCommand) {
        RippleNodeFactory(
            bounded = bounded,
            radius = radius,
            color = color,
            drawCommand = rememberedCommand
        )
    }
}

@Composable
@ExperimentalRippleApi
fun rememberCustomRipple(
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    color: ColorProducer,
    drawCommand: RippleDrawCommand
): IndicationNodeFactory {
    val rememberedCommand = remember { drawCommand }
    return remember(bounded, radius, color, rememberedCommand) {
        RippleNodeFactory(
            bounded = bounded,
            radius = radius,
            colorProducer = color,
            drawCommand = rememberedCommand
        )
    }
}

private val DefaultBoundedRipple = RippleNodeFactory(
    bounded = true,
    radius = Dp.Unspecified,
    color = Color.Unspecified
)
private val DefaultUnboundedRipple = RippleNodeFactory(
    bounded = false,
    radius = Dp.Unspecified,
    color = Color.Unspecified
)