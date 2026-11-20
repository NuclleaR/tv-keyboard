package io.github.nucllear.tvkeyboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints

/**
 * A fixed grid of same-sized keys, laid out in one pass: each key's position follows from its index, so there is
 * nothing to measure twice. Its height comes from [reservedRows], not from the children: the keys arrive a frame
 * after the keyboard (see [TvKeyboard]), and the empty grid already takes the space the full one will.
 */
@Composable
internal fun KeyboardGrid(
  keys: List<KeyboardKey>,
  reservedRows: Int,
  keysPerRow: Int,
  style: KeyboardStyle,
  modifier: Modifier = Modifier,
  key: @Composable (index: Int, key: KeyboardKey) -> Unit,
) {
  Layout(
    content = { keys.forEachIndexed { index, item -> key(index, item) } },
    modifier = modifier,
  ) { measurables, _ ->
    val keyPx = style.keySize.roundToPx()
    val gapPx = style.gap.roundToPx()
    val outerPx = style.outerPadding.roundToPx()
    val step = keyPx + gapPx
    val placeables = measurables.map { it.measure(Constraints.fixed(keyPx, keyPx)) }
    val width = keysPerRow * keyPx + (keysPerRow - 1) * gapPx + outerPx * 2
    val height = if (reservedRows > 0) reservedRows * keyPx + (reservedRows - 1) * gapPx + outerPx * 2 else 0
    layout(width, height) {
      placeables.forEachIndexed { index, placeable ->
        placeable.place(x = outerPx + (index % keysPerRow) * step, y = outerPx + (index / keysPerRow) * step)
      }
    }
  }
}
