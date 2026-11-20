# tv-keyboard

[![](https://jitpack.io/v/NuclleaR/tv-keyboard.svg)](https://jitpack.io/#NuclleaR/tv-keyboard)

An on-screen keyboard for Android TV in Jetpack Compose (TV Material 3), driven by the remote.

- Alphabetical grid: with arrows the viewer can predict where a letter is.
- Opens without a hitch: the keys are built off the main thread and drawn a frame later, into a grid that already
  has its final size; the grid lays out in one pass; the focus styles are built once for all keys.
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
  style = KeyboardDefaults.style(keySize = 36.dp),
)
```

## Develop

```sh
./gradlew :keyboard:testDebugUnitTest :keyboard:lintKotlin
./gradlew :keyboard:publishToMavenLocal   # what JitPack runs for a tag
```
