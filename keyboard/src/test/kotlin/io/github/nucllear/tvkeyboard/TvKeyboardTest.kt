package io.github.nucllear.tvkeyboard

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.isFocused
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.tv.material3.Button
import androidx.tv.material3.Text
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
@Config(qualifiers = "w960dp-h540dp-land-television-xhdpi")
@RunWith(AndroidJUnit4::class)
class TvKeyboardTest {

  @get:Rule
  val compose = createComposeRule()

  // the keys are built off the main thread and arrive a frame later, then the first one takes focus
  private fun awaitKeys() = compose.waitUntil(5_000) { compose.onAllNodes(isFocused()).fetchSemanticsNodes().isNotEmpty() }

  @Test
  fun `types with the remote and erases with backspace`() {
    var text = ""
    compose.setContent {
      TvKeyboard(onCharacter = { text += it }, onBackspace = { text = text.dropLast(1) })
    }
    awaitKeys()
    compose.onNodeWithText("a").assertIsFocused()
    compose.onRoot().performKeyInput {
      pressKey(Key.DirectionCenter)
      pressKey(Key.DirectionRight)
      pressKey(Key.DirectionCenter)
    }
    compose.waitForIdle()
    assertEquals("ab", text)
    compose.onNodeWithContentDescription("Backspace").requestFocus().performKeyInput { pressKey(Key.DirectionCenter) }
    compose.waitForIdle()
    assertEquals("a", text)
  }

  @Test
  fun `the switch key changes the layout and keeps focus`() {
    var text = ""
    compose.setContent {
      TvKeyboard(
        onCharacter = { text += it },
        onBackspace = {},
        layouts = listOf(KeyboardLayouts.Ukrainian, KeyboardLayouts.Latin),
      )
    }
    awaitKeys()
    compose.onNodeWithText("а").assertIsFocused()
    compose.onNodeWithText("ABC").requestFocus().performKeyInput { pressKey(Key.DirectionCenter) }
    compose.waitForIdle()
    compose.onNodeWithText("АБВ").assertIsFocused()
    compose.waitUntil { compose.onAllNodes(androidx.compose.ui.test.hasText("a")).fetchSemanticsNodes().isNotEmpty() }
  }

  @Test
  fun `coming back from outside restores the last key`() {
    compose.setContent {
      var typed by remember { mutableStateOf("") }
      Row {
        TvKeyboard(onCharacter = { typed += it }, onBackspace = {}, space = false)
        Button(onClick = {}) { Text("Result") }
      }
    }
    awaitKeys()
    compose.onRoot().performKeyInput {
      pressKey(Key.DirectionDown) // h
      repeat(6) { pressKey(Key.DirectionRight) } // n, the last key of the row
      pressKey(Key.DirectionRight) // leaves for the result
    }
    compose.onNodeWithText("Result").assertIsFocused()
    compose.onRoot().performKeyInput { pressKey(Key.DirectionLeft) }
    compose.onNodeWithText("n").assertIsFocused()
  }
}
