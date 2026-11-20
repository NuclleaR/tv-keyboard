package io.github.nucllear.tvkeyboard

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class KeyboardLayoutTest {

  private val common = KeyboardLayouts.COMMON.map { it.toString() }

  @Test
  fun `the ukrainian layout is all 33 letters in order plus the common block`() {
    val values = KeyboardLayouts.Ukrainian.keys.map { it.value }
    assertEquals("абвгґдеєжзиіїйклмнопрстуфхцчшщьюя".map { it.toString() }, values.take(33))
    assertEquals(common, values.drop(33))
    listOf("ґ", "є", "і", "ї", "щ", "ь").forEach { assertTrue(it in values, "\"$it\" is missing") }
  }

  @Test
  fun `the latin layout is 26 letters plus the common block`() {
    val values = KeyboardLayouts.Latin.keys.map { it.value }
    assertEquals(26 + common.size, values.size)
    assertEquals(common, values.drop(26))
  }

  @Test
  fun `a custom layout keeps its characters in order`() {
    val github = KeyboardLayout("abc-_./", switchLabel = "ABC")
    assertEquals(listOf("a", "b", "c", "-", "_", ".", "/"), github.keys.map { it.value })
    assertEquals(setOf(KeyType.Character), github.keys.map { it.type }.toSet())
  }

  @Test
  fun `keys are built once`() {
    assertSame(KeyboardLayouts.Latin.keys, KeyboardLayouts.Latin.keys)
  }

  @Test
  fun `rows count a partly filled last row`() {
    assertEquals(0, rowsFor(0, 7))
    assertEquals(1, rowsFor(7, 7))
    assertEquals(2, rowsFor(8, 7))
    assertEquals(6, rowsFor(KeyboardLayouts.Latin.characters.length, 7))
    assertEquals(7, rowsFor(KeyboardLayouts.Ukrainian.characters.length, 7))
  }

  @Test
  fun `the functional row fills the grid width`() {
    for (keysPerRow in 4..9) {
      for (next in listOf(KeyboardLayouts.Latin, null)) {
        for (space in listOf(true, false)) {
          val row = functionalKeys(next, space, keysPerRow)
          assertEquals(keysPerRow, row.sumOf { it.span }, "$keysPerRow per row, switch=${next != null}, space=$space")
          assertTrue(row.all { it.span >= 1 })
        }
      }
    }
  }

  @Test
  fun `the switch key names the layout it leads to and space inserts a space`() {
    val row = functionalKeys(KeyboardLayouts.Latin, space = true, keysPerRow = 7)
    assertEquals(listOf(KeyType.SwitchLayout, KeyType.Space, KeyType.Backspace), row.map { it.type })
    assertEquals(listOf(2, 3, 2), row.map { it.span })
    assertEquals("ABC", row.first().label)
    assertEquals(" ", row[1].value)
    assertEquals("", row.last().value)
  }

  @Test
  fun `one layout has no switch key`() {
    val row = functionalKeys(next = null, space = false, keysPerRow = 7)
    assertEquals(listOf(KeyType.Backspace), row.map { it.type })
    assertEquals(7, row.single().span)
  }
}
