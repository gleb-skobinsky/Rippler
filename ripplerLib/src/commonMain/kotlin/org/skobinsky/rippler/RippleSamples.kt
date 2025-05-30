package org.skobinsky.rippler

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.skobinsky.rippler.node.experimental.ExperimentalRippleApi
import org.skobinsky.rippler.node.opacityRipple.opacityRipple
import org.violet.uiKit.ripple.node.rememberCustomRipple
import org.violet.uiKit.ripple.node.universalRipple
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun UniversalButton() {
    CompositionLocalProvider(
        LocalIndication provides universalRipple()
    ) {
        ClickableBox()
    }
}

@Composable
fun FadingButton() {
    CompositionLocalProvider(
        LocalIndication provides opacityRipple(300, 200)
    ) {
        ClickableBox()
    }
}

@Composable
fun MaterialButton() {
    CompositionLocalProvider(
        LocalIndication provides ripple()
    ) {
        ClickableBox()
    }
}

@OptIn(ExperimentalRippleApi::class)
@Composable
fun StarButton() {
    CompositionLocalProvider(
        LocalIndication provides rememberCustomRipple(
            color = Color.Red,
            radius = 150.dp
        ) { color, center, radius ->
            drawStar(radius, center, color)
        }
    ) {
        ClickableBox(DpSize(300.dp, 300.dp))
    }
}

fun Double.toRadians(): Double = this * PI / 180.0

private fun DrawScope.drawStar(
    radius: Float,
    center: Offset,
    color: Color
) {
    val radiusInner = radius * 0.4f

    val points = mutableListOf<Offset>()

    // A 5-point star has 10 points: 5 outer, 5 inner
    for (i in 0 until 10) {
        val angle = ((i * 36.0) - 90.0).toRadians()
        val r = if (i % 2 == 0) radius else radiusInner
        val x = (center.x + cos(angle) * r).toFloat()
        val y = (center.y + sin(angle) * r).toFloat()
        points.add(Offset(x, y))
    }

    // Draw lines between consecutive points
    for (i in points.indices) {
        drawLine(
            color = color,
            start = points[i],
            end = points[(i + 1) % points.size],
            strokeWidth = 10f
        )
    }
}

@Composable
private fun ClickableBox(
    size: DpSize = DpSize(200.dp, 48.dp)
) {
    Box(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable {}
            .background(Color.LightGray)
            .size(size),
        contentAlignment = Alignment.Center
    ) {
        Text("Button", fontSize = 24.sp)
    }
}

@Preview
@Composable
fun ExampleButtonPreview() {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.Cyan),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UniversalButton()
        MaterialButton()
        FadingButton()
        StarButton()
    }
}

