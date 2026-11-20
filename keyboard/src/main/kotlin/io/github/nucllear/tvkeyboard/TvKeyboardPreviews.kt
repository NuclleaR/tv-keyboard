package io.github.nucllear.tvkeyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

@Composable
@Preview(widthDp = 480, heightDp = 420)
private fun TvKeyboardPreview() {
  MaterialTheme(colorScheme = darkColorScheme()) {
    TvKeyboard(
      onCharacter = {},
      onBackspace = {},
      layouts = listOf(KeyboardLayouts.Ukrainian, KeyboardLayouts.Latin),
      modifier = Modifier.background(MaterialTheme.colorScheme.background).padding(24.dp),
    )
  }
}
