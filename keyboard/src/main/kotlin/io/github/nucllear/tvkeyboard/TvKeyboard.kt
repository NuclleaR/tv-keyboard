package io.github.nucllear.tvkeyboard

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * An on-screen keyboard for TV: a grid of characters driven by the remote, and a row with layout switch, space and
 * backspace.
 *
 * The character keys don't exist on the first composition: they are fetched off the main thread and drawn a frame
 * later, so opening a screen with the keyboard doesn't wait for every key to be built and measured. The grid
 * reserves its full size from the start, so nothing around it jumps.
 *
 * It is one focus group that remembers its last key: leaving for the results and coming back lands where the viewer
 * was. With [focusFirstKey] it takes focus on its first key once the keys arrive.
 */
@Composable
public fun TvKeyboard(
  onCharacter: (String) -> Unit,
  onBackspace: () -> Unit,
  modifier: Modifier = Modifier,
  layouts: List<KeyboardLayout> = listOf(KeyboardLayouts.Latin),
  keysPerRow: Int = 7,
  space: Boolean = true,
  focusFirstKey: Boolean = true,
  style: KeyboardStyle = KeyboardDefaults.style(),
  backspaceDescription: String = "Backspace",
  spaceDescription: String = "Space",
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
  Column(
    modifier = modifier.focusRestorer().focusGroup(),
    verticalArrangement = Arrangement.spacedBy(style.gap - style.outerPadding),
  ) {
    KeyboardGrid(
      keys = keys,
      reservedRows = rowsFor(layout.characters.length, keysPerRow),
      keysPerRow = keysPerRow,
      style = style,
    ) { index, key ->
      Key(
        key = key,
        style = style,
        onClick = { onCharacter(key.value) },
        // focus goes to the first key once it is placed: a request before that, e.g. from an effect right after the
        // keys arrive, is dropped. Only the first time: a later arrival means the viewer pressed the switch key
        modifier = if (index == 0) {
          Modifier.focusRequester(firstKey).onGloballyPositioned {
            if (!focusPlaced) {
              focusPlaced = true
              firstKey.requestFocus()
            }
          }
        } else {
          Modifier
        },
      )
    }
    Row(
      modifier = Modifier.padding(horizontal = style.outerPadding),
      horizontalArrangement = Arrangement.spacedBy(style.gap),
    ) {
      functional.forEach { key ->
        Key(
          key = key,
          style = style,
          description = when (key.type) {
            KeyType.Backspace -> backspaceDescription
            KeyType.Space -> spaceDescription
            else -> null
          },
          onClick = {
            when (key.type) {
              KeyType.SwitchLayout -> layoutIndex = (layoutIndex + 1) % layouts.size
              KeyType.Backspace -> onBackspace()
              KeyType.Space, KeyType.Character -> onCharacter(key.value)
            }
          },
        )
      }
    }
  }
}

@Composable
private fun Key(
  key: KeyboardKey,
  style: KeyboardStyle,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  description: String? = null,
) {
  // a wide key covers the keys it spans and the gaps between them, so it lines up with the grid
  val width = style.keySize * key.span + style.gap * (key.span - 1)
  Surface(
    onClick = onClick,
    modifier = modifier
      .size(width = width, height = style.keySize)
      .then(if (description != null) Modifier.semantics { contentDescription = description } else Modifier),
    shape = style.shape,
    colors = style.colors,
    scale = style.scale,
    border = style.border,
    glow = style.glow,
  ) {
    Box(Modifier.size(width = width, height = style.keySize), contentAlignment = Alignment.Center) {
      Text(key.label, style = style.textStyle)
    }
  }
}
