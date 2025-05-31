package org.skobinsky.rippler.node.drawBasedRipple

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp

typealias RippleDrawCommand = DrawScope.(
    color: Color,
    center: Offset,
    radius: Float
) -> Unit

val SmoothRippleCommand: RippleDrawCommand = { color, center, radius ->
    val correctedRadius = radius * 1.5f
    drawCircle(
        brush = Brush.radialGradient(
            listOf(
                color,
                color,
                Color.Transparent
            ),
            center = center,
            radius = correctedRadius
        ),
        radius = correctedRadius,
        center = center
    )
}

val CircularRippleCommand: RippleDrawCommand = { color, center, radius ->
    drawCircle(
        color = color,
        radius = radius,
        center = center
    )
}

val WaterRippleCommand: RippleDrawCommand = { color, center, radius ->
    val waterRippleWidth = 10.dp.toPx()
    val waterRippleFraction = waterRippleWidth / radius

    drawCircle(
        brush = Brush.radialGradient(
            (1f - (waterRippleFraction * 4)) to Color.Transparent,
            (1f - (waterRippleFraction * 2)) to color,
            1f to Color.Transparent,
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center
    )
}