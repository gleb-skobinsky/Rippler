package org.skobinsky.rippler.node.drawBasedRipple

import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
class RippleConfig(
    val color: Color = Color.Unspecified,
    val rippleAlpha: RippleAlpha? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RippleConfig) return false

        if (color != other.color) return false
        if (rippleAlpha != other.rippleAlpha) return false

        return true
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + (rippleAlpha?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "RippleConfiguration(color=$color, rippleAlpha=$rippleAlpha)"
    }
}

val LocalRippleConfig = compositionLocalOf<RippleConfig?> { RippleConfig() }