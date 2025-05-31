package org.skobinsky.rippler.node.drawBasedRipple

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

typealias RippleDrawCommand = DrawScope.(
    color: Color,
    center: Offset,
    radius: Float
) -> Unit

val SmoothRippleCommand: RippleDrawCommand = { color, center, radius ->
    val correctedRadius = radius * 1.33f
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