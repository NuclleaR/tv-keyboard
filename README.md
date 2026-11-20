# tv-keyboard

[![](https://jitpack.io/v/NuclleaR/tv-keyboard.svg)](https://jitpack.io/#NuclleaR/tv-keyboard)

An on-screen keyboard for Android TV in Jetpack Compose (TV Material 3), driven by the remote.

- Alphabetical grid: with arrows the viewer can predict where a letter is.
- Behavior only, no look: every key is drawn by your slot, which gets the key, its click and a modifier with its
  size and focus.
- Opens without a hitch: the keys are built off the main thread and drawn a frame later, into a grid that already
  has its final size; the grid lays out in one pass.
- One focus group that remembers its last key: going to the results and back lands where you were.
- Layouts you define (`KeyboardLayout("abc…-_./", switchLabel = "ABC")`), presets `KeyboardLayouts.Latin` and
  `KeyboardLayouts.Ukrainian`, a switch key when there are several.

## Use

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
  repositories { maven("https://jitpack.io") }
}

// build.gradle.kts
implementation("com.github.NuclleaR:tv-keyboard:<tag>")
```

```kotlin
TvKeyboard(
  onCharacter = { query += it },
  onBackspace = { query = query.dropLast(1) },
  layouts = listOf(KeyboardLayouts.Ukrainian, KeyboardLayouts.Latin),
) { key, onClick, modifier ->
  // the keyboard draws nothing itself: any key you like, as long as it applies the modifier
  Surface(onClick = onClick, modifier = modifier) { Text(key.label) }
}
```

## Develop

```sh
./gradlew :keyboard:testDebugUnitTest :keyboard:lintKotlin
./gradlew :keyboard:publishToMavenLocal   # what JitPack runs for a tag
```
