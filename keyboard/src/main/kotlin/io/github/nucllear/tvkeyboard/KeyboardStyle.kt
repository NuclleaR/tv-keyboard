package io.github.nucllear.tvkeyboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceBorder
import androidx.tv.material3.ClickableSurfaceColors
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ClickableSurfaceGlow
import androidx.tv.material3.ClickableSurfaceScale
import androidx.tv.material3.ClickableSurfaceShape
import androidx.tv.material3.MaterialTheme

/**
 * Everything about how the keys look. Built once for the whole keyboard and handed to every key: the
 * `ClickableSurfaceDefaults` builders create new objects on each call, which per key added up to hundreds of
 * allocations for one keyboard.
 */
@Immutable
public class KeyboardStyle(
  public val keySize: Dp,
  public val gap: Dp,
  /** Room around the grid for the focus ring (4dp) and scale of the outer keys, which have no gap to borrow from. */
  public val outerPadding: Dp,
  public val shape: ClickableSurfaceShape,
  public val colors: ClickableSurfaceColors,
  public val scale: ClickableSurfaceScale,
  public val border: ClickableSurfaceBorder,
  public val glow: ClickableSurfaceGlow,
  public val textStyle: TextStyle,
)

public object KeyboardDefaults {

  /** Defaults from the TV Material theme: surface keys, the primary color with a ring outside when focused. */
  @Composable
  public fun style(
    keySize: Dp = 40.dp,
    gap: Dp = 8.dp,
    outerPadding: Dp = 6.dp,
    shape: Shape = RoundedCornerShape(8.dp),
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    focusedContainerColor: Color = MaterialTheme.colorScheme.primary,
    focusedContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    focusedScale: Float = 1.1f,
    ringColor: Color = MaterialTheme.colorScheme.onSurface,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
  ): KeyboardStyle {
    val clickableShape = ClickableSurfaceDefaults.shape(shape)
    val colors = ClickableSurfaceDefaults.colors(
      containerColor = containerColor,
      contentColor = contentColor,
      focusedContainerColor = focusedContainerColor,
      focusedContentColor = focusedContentColor,
      pressedContainerColor = focusedContainerColor,
      pressedContentColor = focusedContentColor,
    )
    val scale = ClickableSurfaceDefaults.scale(focusedScale = focusedScale)
    val border = ClickableSurfaceDefaults.border(
      // 2dp wide, 2dp outside the key: the inset moves the stroke's center out of the edge
      focusedBorder = Border(BorderStroke(2.dp, ringColor), inset = 3.dp, shape = shape),
    )
    val glow = ClickableSurfaceDefaults.glow()
    return remember(keySize, gap, outerPadding, clickableShape, colors, scale, border, glow, textStyle) {
      KeyboardStyle(keySize, gap, outerPadding, clickableShape, colors, scale, border, glow, textStyle)
    }
  }
}
