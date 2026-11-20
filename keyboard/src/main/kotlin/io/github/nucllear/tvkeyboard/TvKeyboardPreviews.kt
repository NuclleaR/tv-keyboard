package io.github.nucllear.tvkeyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// the library draws nothing of its own: the app's keys go in the slot, here the plainest possible ones
@Composable
@Preview(widthDp = 480, heightDp = 420)
private fun TvKeyboardPreview() {
  Box(Modifier.background(Color(0xFF111318)).padding(24.dp)) {
    TvKeyboard(
      onCharacter = {},
      onBackspace = {},
      layouts = listOf(KeyboardLayouts.Ukrainian, KeyboardLayouts.Latin),
      focusFirstKey = false,
    ) { key, onClick, modifier ->
      Box(
        modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFF282A2F)).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
      ) {
        BasicText(key.label, style = TextStyle(color = Color.White, fontSize = 16.sp))
      }
    }
  }
}
