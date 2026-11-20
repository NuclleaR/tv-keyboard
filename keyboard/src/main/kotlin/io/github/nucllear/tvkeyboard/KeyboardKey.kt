package io.github.nucllear.tvkeyboard

import androidx.compose.runtime.Immutable

/**
 * One key. [label] is what the viewer reads, [value] is what lands in the text; they differ only for the
 * functional keys, where `␣` inserts a space and `⌫` inserts nothing.
 */
@Immutable
public data class KeyboardKey(
  val value: String,
  val label: String = value,
  val type: KeyType = KeyType.Character,
  val span: Int = 1,
)

public enum class KeyType { Character, Space, Backspace, SwitchLayout }

/**
 * A set of characters shown as a grid, in the given order. Alphabetical order beats ЙЦУКЕН or QWERTY on a TV:
 * driven by arrows, the viewer can predict where a letter is.
 *
 * [switchLabel] names this layout on the switch key of the layout before it (`ABC`, `АБВ`). The keys are built
 * once, on first use, and the same list is returned for every later visit and every switch back.
 */
@Immutable
public class KeyboardLayout(
  public val characters: String,
  public val switchLabel: String,
) {
  public val keys: List<KeyboardKey> by lazy { characters.map { KeyboardKey(value = it.toString()) } }

  override fun equals(other: Any?): Boolean = other is KeyboardLayout && other.characters == characters && other.switchLabel == switchLabel

  override fun hashCode(): Int = 31 * characters.hashCode() + switchLabel.hashCode()
}

public object KeyboardLayouts {

  /** Shared by the alphabets below, so a digit or a full stop never costs a layout switch. */
  public const val COMMON: String = "-!?.,#0123456789"

  public val Latin: KeyboardLayout = KeyboardLayout("abcdefghijklmnopqrstuvwxyz$COMMON", switchLabel = "ABC")

  /** All 33 letters of the Ukrainian alphabet, in order. */
  public val Ukrainian: KeyboardLayout = KeyboardLayout("абвгґдеєжзиіїйклмнопрстуфхцчшщьюя$COMMON", switchLabel = "АБВ")
}

/** How many rows [count] keys occupy when [keysPerRow] fit in one. */
public fun rowsFor(
  count: Int,
  keysPerRow: Int,
): Int = (count + keysPerRow - 1) / keysPerRow

/**
 * The row under the grid: a switch to the next layout when there is more than one, a space when [space] is set,
 * and a backspace. The keys share the row's [keysPerRow] spans between them, so the row is as wide as the grid.
 */
public fun functionalKeys(
  next: KeyboardLayout?,
  space: Boolean,
  keysPerRow: Int,
): List<KeyboardKey> {
  val switch = next?.let { KeyboardKey(value = "", label = it.switchLabel, type = KeyType.SwitchLayout) }
  val spaceKey = if (space) KeyboardKey(value = " ", label = "␣", type = KeyType.Space) else null
  val backspace = KeyboardKey(value = "", label = "⌫", type = KeyType.Backspace)
  val keys = listOfNotNull(switch, spaceKey, backspace)
  // the space key takes what's left after two-key switch and backspace; without it, the others split the row
  val side = if (spaceKey != null) ((keysPerRow - 1) / 3).coerceAtLeast(1) else keysPerRow / keys.size
  return keys.map { key ->
    val span = when {
      key.type == KeyType.Space -> keysPerRow - side * (keys.size - 1)
      key === keys.last() && spaceKey == null -> keysPerRow - side * (keys.size - 1)
      else -> side
    }
    key.copy(span = span)
  }
}
