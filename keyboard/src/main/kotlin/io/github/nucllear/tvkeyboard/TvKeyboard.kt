package io.github.nucllear.tvkeyboard

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * An on-screen keyboard for TV: a grid of characters driven by the remote, and a row with layout switch, space and
 * backspace. It owns the behavior and none of the look: every key is drawn by [key], which gets the key, what to do
 * when it is clicked, and a modifier that it must apply to its focusable element (the key's size and, for the first
 * key, its initial focus).
 *
 * The character keys don't exist on the first composition: they are fetched off the main thread and drawn a frame
 * later, so opening a screen with the keyboard doesn't wait for every key to be built and measured. The grid
 * reserves its full size from the start, so nothing around it jumps.
 *
 * It is one focus group that remembers its last key: leaving for the results and coming back lands where the viewer
 * was. With [focusFirstKey] it takes focus on its first key once the keys arrive.
 *
 * [keySize] and [gap] place the keys; [outerPadding] is room around the grid for whatever a focused key draws beyond
 * its bounds (a scale, a ring), which the outer keys have no gap to borrow from.
 */
// key draws one key per call, like a lazy list's item content, not a single piece of content moved between places
@Suppress("ktlint:compose:content-slot-reused")
@Composable
public fun TvKeyboard(
  onCharacter: (String) -> Unit,
  onBackspace: () -> Unit,
  modifier: Modifier = Modifier,
  layouts: List<KeyboardLayout> = listOf(KeyboardLayouts.Latin),
  keysPerRow: Int = 7,
  space: Boolean = true,
  focusFirstKey: Boolean = true,
  keySize: Dp = 40.dp,
  gap: Dp = 8.dp,
  outerPadding: Dp = 6.dp,
  key: @Composable (key: KeyboardKey, onClick: () -> Unit, modifier: Modifier) -> Unit,
) {
  require(layouts.isNotEmpty()) { "at least one layout" }
  var layoutIndex by remember { mutableIntStateOf(0) }
  val layout = layouts[layoutIndex.coerceIn(layouts.indices)]
  var keys by remember { mutableStateOf(emptyList<KeyboardKey>()) }
  val firstKey = remember { FocusRequester() }
  var focusPlaced by remember { mutableStateOf(!focusFirstKey) }

  LaunchedEffect(layout) {
    keys = withContext(Dispatchers.Default) { layout.keys }
  }
  val next = if (layouts.size > 1) layouts[(layoutIndex + 1) % layouts.size] else null
  val functional = remember(next, space, keysPerRow) { functionalKeys(next, space, keysPerRow) }

  // a wide key covers the keys it spans and the gaps between them, so it lines up with the grid
  fun Modifier.keySize(span: Int) = size(width = keySize * span + gap * (span - 1), height = keySize)

  Column(
    modifier = modifier.focusRestorer().focusGroup(),
    verticalArrangement = Arrangement.spacedBy(gap - outerPadding),
  ) {
    KeyboardGrid(
      keys = keys,
      reservedRows = rowsFor(layout.characters.length, keysPerRow),
      keysPerRow = keysPerRow,
      keySize = keySize,
      gap = gap,
      outerPadding = outerPadding,
    ) { index, item ->
      // focus goes to the first key once it is placed: a request before that, e.g. from an effect right after the
      // keys arrive, is dropped. Only the first time: a later arrival means the viewer pressed the switch key
      val focus = if (index == 0) {
        Modifier.focusRequester(firstKey).onGloballyPositioned {
          if (!focusPlaced) {
            focusPlaced = true
            firstKey.requestFocus()
          }
        }
      } else {
        Modifier
      }
      key(item, { onCharacter(item.value) }, focus.keySize(item.span))
    }
    Row(
      modifier = Modifier.padding(horizontal = outerPadding),
      horizontalArrangement = Arrangement.spacedBy(gap),
    ) {
      functional.forEach { item ->
        val onClick = {
          when (item.type) {
            KeyType.SwitchLayout -> layoutIndex = (layoutIndex + 1) % layouts.size
            KeyType.Backspace -> onBackspace()
            KeyType.Space, KeyType.Character -> onCharacter(item.value)
          }
        }
        key(item, onClick, Modifier.keySize(item.span))
      }
    }
  }
}
