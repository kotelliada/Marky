# Marky — Agent Guide

This file is the system prompt for every agent session in this repo. `CLAUDE.md` is a symlink to it. Keep it short and current: when a decision in here changes, update this file in the same commit.

---

## 1. What Marky is

Marky is a **local-first Markdown notes app** for **Android, iOS and Desktop (JVM)**, built with Kotlin Multiplatform and Compose Multiplatform. People write notes in Markdown, read them formatted, and find any note quickly through search. It runs fully offline, with no account, server or sync. All notes stay on the device, and any single note can be imported or exported as a plain `.md` file.

**Principles:** writing comes first (a new note takes one tap), formatting lives in the text (typed Markdown, not menus), and the user owns their data.

**Feature areas**
1. **Managing notes:** create (one action opens an empty note for writing), autosave (no save button), a list sorted by last edited (title, plain-text preview, time since edit, pin indicator), pin to top, delete with confirmation.
2. **Writing in Markdown:** raw Markdown editor (headings, bold, italic, lists, checklists, quotes, code blocks, web links); a formatting toolbar that types the same syntax and wraps the selection; Edit/Preview switch; an interactive preview (tick checklists, which writes `[x]` back to the source; links open in the browser).
3. **Search:** full-text over titles and bodies, with title matches ranked above text matches, and snippets with the matches highlighted.
4. **Import / export:** import one `.md` file as a new note (title = file name without `.md`, no bulk import, duplicate titles allowed, the user stays on the list and the note appears at the top of the unpinned notes, failures show a snackbar). Export one note as `<title>.md` from the note screen.
5. **General:** sample notes on first launch, light/dark theme following the system, helpful empty states.

**Screens:** Notes list (home), Note (Edit / Preview), Search, plus the Delete confirmation dialog. Every screen is one step from the list.

**Out of scope (do not build):** accounts, sync, sharing, collaboration, any backend · links between notes · bulk import/export · images and attachments · daily notes and templates · version history and undo delete · tags · folders · encryption and app lock.

---

## 2. Sources of truth

| What | Where | Notes |
|---|---|---|
| Product behavior | [`marky-feature-specification.md`](marky-feature-specification.md) | Local mirror of the spec at https://markdown-editor.notion.site/ (the Notion page renders only with JS, so read the local file). The spec decides behavior. |
| **All UI** | [`designs/margin/`](designs/margin/) | **Single source of truth for every piece of UI.** See section 3. |
| Conventions | [`.claude/skills/`](.claude/skills/) | How we build things. See section 5. |
| Versions | [`gradle/libs.versions.toml`](gradle/libs.versions.toml) | The only place versions are declared. |

Design files:
- `designs/margin/design-system.html`: fonts (IBM Plex Sans / Mono), color tokens with hex values for light and dark, spacing, radius, elevation, motion, sizes, icons, **every reusable component in every state**, Markdown nodes, platform differences, keyboard shortcuts, the Margin token → Compose / Material 3 mapping, and UX copy ("Writing for Marky").
- `designs/margin/prototype.html` + `marky.js`: every screen and screen state (list default, nothing pinned, empty, after import, import failed; note preview, edit, selection, new, empty preview, menu, delete, exported; search start, results, no results), on phone and desktop, with the interaction logic (sorting, search ranking, snippets, relative times).
- `designs/margin/tokens.css`, `components.css`: exact values.
- `designs/brainstorm/` is earlier exploration. It is **not** a source of truth.

---

## 3. UI rules (hard rules)

- **Never invent UI.** Every screen, component, state, spacing, color, icon, animation and string must trace back to `designs/margin/`.
- **Feature code uses only Marky design-system components** from `:core:designsystem` (`MarkyTheme`, `MarkyColors`, `MarkyTypography`, `MarkyIcons`, `MarkyDimens`, and the Margin components). It never uses raw Material 3 or foundation components for visible UI. Material 3 is allowed only *inside* `:core:designsystem`, as the base a Margin component wraps (see kmp-compose-components and the design system's "Mapping to Compose").
- **If a design is missing or ambiguous, stop, flag it and ask** how to proceed. Name the screen, state or component and what is missing. Never ship placeholder or "reasonable default" UI.
- UX copy comes from the design system's "Writing for Marky" section, through string resources.
- Markdown is parsed with **JetBrains Markdown** (`org.jetbrains:markdown`). The preview is custom Compose built from its AST to match the design's "Markdown nodes" section. No third-party Markdown renderer.

---

## 4. Tech stack

Adding any library not listed here requires the user's agreement first.

### In use

| Concern | Library |
|---|---|
| Platform | Kotlin Multiplatform: Android, iOS (arm64 and simulator arm64), Desktop JVM |
| UI | Compose Multiplatform + Compose resources (`Res.string`, `Res.drawable`, `Res.font`) |
| Design-system base | Material 3 (CMP), used only inside `:core:designsystem` |
| Adaptive layout | Material3 Adaptive (JetBrains): phone single pane vs desktop sidebar + note pane |
| Markdown | JetBrains Markdown `org.jetbrains:markdown` |
| DI | Koin: `koin-core`, `koin-compose`, `koin-compose-viewmodel`, `koin-android` |
| Navigation | JetBrains Navigation Compose `org.jetbrains.androidx.navigation:navigation-compose`, type-safe `@Serializable` routes |
| Presentation | JetBrains lifecycle: `lifecycle-viewmodel`, `lifecycle-viewmodel-compose`, `lifecycle-runtime-compose`, `lifecycle-viewmodel-savedstate`, `savedstate` |
| Serialization | kotlinx.serialization (nav routes and the notes index) |
| Async | kotlinx.coroutines + Flow |
| Storage | **Okio**: each note is `<id>.md` (raw Markdown only), and one `notes-index.json` holds `id, title, pinned, createdAt, editedAt` |
| File pickers | Hand-written `expect`/`actual`: Android SAF document picker, iOS Files picker, Desktop native open/save dialog |
| Time | kotlinx-datetime + an injected `kotlin.time.Clock` |
| Logging | Kermit |
| Build | Gradle 9, AGP `com.android.kotlin.multiplatform.library`, convention plugins in `build-logic`, version catalog |

### Testing

| Concern | Library |
|---|---|
| Framework | `kotlin.test` in `commonTest` |
| Assertions | AssertK |
| Flows | Turbine |
| Coroutines | `kotlinx-coroutines-test`, `UnconfinedTestDispatcher` |
| File system | Okio `FakeFileSystem` (in memory, so the tests stay unit tests) |
| Doubles | Hand-written fakes. No MockK, Mockito or JUnit5. |
| Coverage | Kover: reports only, not a gate |

### Skill defaults we deliberately do **not** use

| Library | Why not |
|---|---|
| Ktor | No backend or network |
| Room | Notes are `.md` files on disk |
| DataStore | No settings; the theme follows the system |
| Coil | No images (out of scope) |
| Moko Permissions | Document pickers need no runtime permissions |
| BuildKonfig | No secrets or API keys |
| WorkManager | No background work |
| Compose UI test, Espresso, AndroidX test runner | UI and instrumented tests are out of scope |

---

## 5. Skill map: which skill to invoke when

Invoke the matching skill **before** writing the code. Several can apply at once.

| Skill | Invoke when |
|---|---|
| `kmp-module-structure` | Adding or moving modules, Gradle and convention plugins, the version catalog, "where does X live?" |
| `kmp-presentation-mvi` | Any ViewModel, State/Action/Event, Root/Screen composable, UiText, SavedStateHandle |
| `kmp-compose-architecture` | Screen structure, recomposition and stability, state ownership, side effects, lazy lists, TextField state (the editor) |
| `kmp-compose-components` | Any non-screen composable, above all `:core:designsystem` components, previews, icons, string resources |
| `kmp-navigation` | Routes, nav graphs, cross-feature navigation, wiring in `:composeApp` |
| `kmp-di-koin` | Koin modules, injection, `startKoin` on Android, iOS and Desktop |
| `kmp-data-layer` | Data sources, repositories, mappers. Apply its patterns to Okio + the JSON index, not Ktor or Room. |
| `kmp-error-handling` | `Result`, `DataError`, typed errors (import failures, file I/O, validation) |
| `kmp-unit-testing` | Every unit test. Start with its break-vector method. |

The skills mention `kmp-integration-testing`, `kmp-ui-testing`, `kmp-kover-setup`, `compose-adaptive-layouts`, `android-background` and `kotlin-mutation-testing`. They are **not installed** in this repo; ignore those references. Mutation testing runs only when the user explicitly asks for it.

---

## 6. Project map: where things live and where they go

Layout (everything exists except `:core:markdown`, which is created when first needed; feature modules start as empty skeletons):

```
:androidApp                   Thin Android shell: Activity, manifest. Depends on :composeApp only.
:desktopApp                   Thin desktop shell: main(), Window. Depends on :composeApp only.
iosApp/                       Xcode shell consuming the ComposeApp framework.
:composeApp                   App(), NavHost, Koin assembly, MarkyTheme applied. Wires everything.
build-logic/                  Convention plugins (kmp.library, cmp.library, cmp.feature, cmp.application, android.application.compose).
:core:domain                  Result/DataError, Note model, NoteRepository interface, Clock abstraction.
:core:data                    Okio note store (<id>.md + notes-index.json), file helpers, sample notes.
:core:presentation            UiText, ObserveAsEvents, expect/actual import/export pickers.
:core:designsystem            Margin: theme, colors, typography (IBM Plex fonts), icons, dimens, all components.
:core:markdown                JetBrains Markdown parsing, plain text for previews and search, toolbar text transforms.
:feature:<name>:domain|data|presentation
```

Features: `notes` (list + import), `note` (edit, preview, export, delete), `search`. Ask before adding another.

Convention plugins (`build-logic/convention`, IDs `io.kotelliada.marky.convention.*`, aliases `libs.plugins.convention.*`):
- `kmp.library`: `core:domain`, `core:data`, every feature `domain` and `data`. Targets: Android (KMP library + host tests), Desktop `jvm("desktop")`, `iosArm64`, `iosSimulatorArm64`.
- `cmp.library`: `core:presentation`, `core:designsystem`. Adds Compose without Material 3, so **only `:core:designsystem` declares `material3`**, which enforces section 3 at build level.
- `cmp.feature`: every feature `presentation`. Adds `core:domain`/`core:presentation`/`core:designsystem`, Koin, navigation, lifecycle and savedstate.
- `cmp.application`: `:composeApp` (iOS framework `ComposeApp`).
- `android.application.compose`: `:androidApp`.

Project-wide utilities: `Result`/`EmptyResult`/`Error`/`DataError` (plus `map`, `onSuccess`, `onFailure`, `asEmptyResult`) live in `core:domain` (`io.kotelliada.marky.core.domain.util`). `UiText` and `ObserveAsEvents` live in `core:presentation` (`io.kotelliada.marky.core.presentation.util`). `DataError` has only `Local`, because there is no backend. `DataError.toUiText()` is added once a feature needs it and the design system has the copy.

Rules:
- Features **never** depend on each other. They navigate through callbacks wired in `:composeApp`.
- Code lives in its feature until **two or more** features need it, then it moves to the right `core` module.
- `domain` depends on `core:domain` only. `presentation` may use its own domain plus `core:domain`, `core:presentation` and `core:designsystem`.
- Strings, drawables and fonts live in the owning module's `src/commonMain/composeResources/`.
- Platform code goes in `androidMain` / `iosMain` / `desktopMain` behind `expect`/`actual` or an interface.
- No hard-coded versions; everything goes through `gradle/libs.versions.toml`.

---

## 7. Working agreement

### Never assume what we're building
- If the spec or the designs don't answer a **product, UI or scope** question, **ask before building**. Don't fill the gap yourself.
- If a **technical** assumption is unavoidable and cheap to reverse, you may proceed, but you must report it. End every response that made one with an **`## Assumptions`** section, one item each:
  - **Assumed:** what you assumed
  - **Why:** what was missing
  - **Alternative:** what else it could be
  - **To change:** what the user would need to say or undo
- Never bury an assumption in prose. The user wants the chance to intervene.

### Commit automatically
- When a unit of work meets the Definition of Done, **commit it right away** on the current branch, without being asked.
- Make one focused commit per unit of work: an imperative subject line, then a short body explaining *why*.
- Stage explicit paths. Never use `git add -A` or `git add .`.
- Never commit `local.properties`, secrets, IDE files or build output.
- Don't commit failing or half-done work. Don't push, amend or force-push unless asked.

---

## 8. Definition of Done

A unit of work is done only when **all** of the following hold:

1. **Spec:** the behavior matches `marky-feature-specification.md`.
2. **Design:** the UI matches `designs/margin/` for every affected state on phone and desktop, in light and dark. Every design gap was flagged and resolved with the user.
3. **Clean build on all three platforms**, with no errors and no new warnings:
   - Android: `./gradlew :androidApp:assembleDebug`
   - Desktop: `./gradlew :desktopApp:assemble`
   - iOS: `./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64`, then
     `xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -sdk iphonesimulator -configuration Debug build`
4. **Local unit tests pass** on all targets: `./gradlew allTests testAndroidHostTest`
5. **New or changed logic has unit tests** in `commonTest`, written per `kmp-unit-testing` (break vectors first: unicode and emoji, boundaries, ordering). This covers ViewModels, the note store and index, Markdown transforms, search ranking and snippets, time formatting, import title rules and sorting.
6. **Conventions:** the relevant skills were followed, module dependency rules hold, strings come from resources, and each component has a preview per state.
7. **Reported and committed:** assumptions are listed and the work is committed (see section 7).

**Explicitly out of scope, so don't write or configure these:** UI tests, Compose UI tests, instrumented/device tests (`androidDeviceTest`, `connectedAndroidDeviceTest`), screenshot tests, integration tests, end-to-end tests. We test with **local unit tests only**.

---

## 9. Quick commands

| Task | Command |
|---|---|
| Run desktop | `./gradlew :desktopApp:run` (hot reload: `./gradlew :desktopApp:hotRun --auto`) |
| Build Android | `./gradlew :androidApp:assembleDebug` |
| Run iOS | Open `iosApp/` in Xcode and run |
| All unit tests | `./gradlew allTests testAndroidHostTest` |
| One module's tests | `./gradlew :<module>:testAndroidHostTest`, `:<module>:desktopTest`, `:<module>:iosSimulatorArm64Test` |
| Coverage report | `./gradlew koverHtmlReport` (once Kover is set up) |

On an Intel Mac, `iosSimulatorArm64Test` is disabled by Kotlin/Native (host can't run arm64). iOS tests then run only on Apple Silicon.
