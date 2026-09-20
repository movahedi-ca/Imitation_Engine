package com.example.ui.components

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * High-performance Progressive Blur Shader:
 * Caches the RenderEffect instance on Android 12+ (API 31+) to eliminate native heap allocations
 * during draw passes. Returns directly if blur radius is near zero.
 */
fun Modifier.progressiveBlurShader(blurRadiusDp: Float): Modifier {
  if (blurRadiusDp <= 0.5f) {
    return this
  }

  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    var cachedRadiusPx = -1f
    var cachedEffect: androidx.compose.ui.graphics.RenderEffect? = null

    this.graphicsLayer {
      val radiusPx = blurRadiusDp * density
      if (radiusPx != cachedRadiusPx || cachedEffect == null) {
        cachedRadiusPx = radiusPx
        cachedEffect = RenderEffect.createBlurEffect(
          radiusPx,
          radiusPx,
          Shader.TileMode.CLAMP
        ).asComposeRenderEffect()
      }
      renderEffect = cachedEffect
    }
  } else {
    this.blur(blurRadiusDp.dp)
  }
}

/**
 * Overload accepting Dp for convenient Compose usage
 */
fun Modifier.progressiveBlurShader(blurRadius: Dp): Modifier =
  progressiveBlurShader(blurRadius.value)

