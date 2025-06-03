This is a Kotlin Multiplatform and Compose Multiplatform library aimed at providing extended support for various click indications.

It supports Android, iOS, Desktop (JVM) and Web (Wasm Canvas-based API).

# Installation

In order to install the library with Gradle, be sure to use the `mavenCentral()` repository in your project.

Then simply use it in any of your source sets (including, but not limited to, `commonMain`):

```
implementation("io.github.gleb-skobinsky:rippler:1.0.3")
```

# Standard usage

The library provides three methods to create an `IndicationNodeFactory`: `universalRipple`, `circularRipple`, and `opacityRipple`.

The former two methods are very similar to the `ripple` method from Material UI and can be used like this:

```
CompositionLocalProvider(
  LocalIndication provides universalRipple(
      bounded = true,
      radius = Dp.Unspecified,
      startRadiusFraction: Float = DefaultSoftRadius,
      color = Color.Unspecified,
      animations = RippleAnimationDuration.Default
  )
) {
  // ...
}
```
Universal ripple behaves like Android 15 default ripple, while circularRipple comes directly from Compose Multiplatform Skiko implementation.

Note that when the color argument is passed as Undefined, the underlying RippleNode will read the color and alpha from `org.skobinsky.rippler.node.drawBasedRipple.LocalRippleConfig`. It behaves very similarly to Material's LocalRippleConfiguration, but is used to decouple the library from Material and let you use it without Material dependencies.

Opacity ripple is different and allows your clickable component to fade out on click and then fade in back on release:

```
CompositionLocalProvider(
  LocalIndication provides opacityRipple(
            fadeInDuration = 280,
            fadeOutDuration = 0,
            minAlpha = 0.25f
        )
) {
  // ...
}
```
The arguments provided in the example above will produce an effect looking very similar to a native iOS SwiftUI button.

# Experimental usage

The library allows you to fully customize the drawing command used to define how the ripple looks like:

```
CompositionLocalProvider(
  LocalIndication provides rememberCustomRipple  { color, center, radius ->
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
) {
  // ...
}
```
Note that the lambda passed in as the drawCommand must not contain any external objects. It will be remembered without keys internally. It is a performance optimization to leave no room for endless lambda recreations and node re-attachments.

The sample code above will produce the following water-like ripple effect:

https://github.com/user-attachments/assets/70c22a2a-2622-4842-b640-7c78217f31ec


