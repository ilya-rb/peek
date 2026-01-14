## Tech Stack

- **Language**: Kotlin Multiplatform
- **UI Framework**: Jetbrains Compose Multiplatform
- **Architecture**: Circuit (Slack's Compose-first architecture)
- **Dependency Injection**: Metro
- **Networking**: Ktor
- **Database**: SQLDelight
- **Async/State**: Kotlin Coroutines + Flows
- **Image Loading**: Coil 3

## Project Structure

```
peek/
├── app/                    # Android application module
├── core/                   # Shared core modules
├── features/               # App feature modules
├── peek-api/               # Module for news data
├── ui-kit/                 # Design system and app resources
├── gradle/
│   ├── build-logic/        # Convention plugins
│   └── libs.versions.toml  # Version catalog
└── iosApp/                 # iOS application
```

## Code style

- .editorconfig is the source of truth for formatting

## Class layout

MUST be in this order:

1. Properties: public/internal/protected/private in this order
2. Initializer blocks
3. Secondary constructors
4. Methods: same order as properties
5. Companion object

## Architecture Patterns

### Circuit (Screen/Presenter/UI Pattern)

Each screen follows Circuit's architecture with three components:

1. **Screen**: A `@Parcelize` data class representing navigation destination
2. **Presenter**: Handles business logic, produces `State` via `@Composable present()`
3. **UI**: `@Composable` function that renders the `State`

```kotlin
// Screen definition (in navigation-map module)
@Parcelize
public data object HomeScreen : Screen

// Contract (State + Events)
internal interface HomeScreenContract {
  @Immutable
  data class State(
    val eventSink: (Event) -> Unit,
  ) : CircuitUiState

  sealed interface Event : CircuitUiEvent {
    data class ArticleClicked(val article: Article) : Event
  }
}

// Presenter
internal class HomeScreenPresenter : Presenter<HomeScreenContract.State> {

  @Composable
  override fun present(): HomeScreenContract.State {
    return HomeScreenContract.State(
      eventSink = { event ->
        when (event) {
          is Event.ArticleClicked -> navigator.goTo(ReaderScreen(event.article.url))
        }
      }
    )
  }
}

// UI Component
@Composable
internal fun HomeScreen(state: HomeScreenContract.State, modifier: Modifier) {
  // Render UI based on state
}
```

### Factory Registration (Metro DI)

Features register their UI and Presenter factories using Metro's `@ContributesIntoSet`:

```kotlin
public interface HomeScreenComponent {

  @Inject
  @ContributesIntoSet(UiScope::class)
  public class ScreenFactory : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
      return if (screen is HomeScreen) {
        ui<State> { state, modifier -> HomeScreen(state, modifier) }
      } else null
    }
  }

  @Inject
  @ContributesIntoSet(UiScope::class)
  public class PresenterFactory(
    private val apiService: PeekApiService,
  ) : Presenter.Factory {
    override fun create(
      screen: Screen,
      navigator: Navigator,
      context: CircuitContext
    ): Presenter<*>? {
      return if (screen is HomeScreen) {
        HomeScreenPresenter(navigator, apiService)
      } else null
    }
  }
}
```

### Async Data Loading

- Use the `Async<T>` sealed class for representing loading states
- Use `AsyncDataStore` for cache-first data loading with appropriate strategies

### Dependency Injection (Metro)

Use `@BindingContainer` to provide app level dependencies

```kotlin
@BindingContainer(includes = [PlatformBindings::class])
object FeatureBindings {

  @Provides
  fun provideService(repository: Repository): Service {
    return DefaultService(repository)
  }

  @Provides
  @SingleIn(AppScope::class)
  fun provideDatabase(driver: SqlDriver): Database {
    return Database(driver)
  }
}

// Platform-specific bindings
@BindingContainer
expect abstract class PlatformBindings
```

Scopes:

- `@AppScope` - Application-level singleton
- `@UiScope` - UI component scope
- Use `@Qualifier` annotations for disambiguating same-type bindings

### Kotlin Multiplatform Source Sets

```
src/
├── commonMain/      # Shared code
├── commonTest/      # Shared tests
├── androidMain/     # Android-specific implementations
├── iosMain/         # iOS-specific implementations
```

Use `expect`/`actual` for platform-specific code:

```kotlin
// commonMain
expect fun getPlatformName(): String

// androidMain
actual fun getPlatformName(): String = "Android"

// iosMain
actual fun getPlatformName(): String = "iOS"
```

## Testing

### Test Location

Tests are placed in `src/commonTest/kotlin/` mirroring the main source structure:

```
core/data/src/
├── commonMain/kotlin/com/illiarb/peek/core/data/
│   └── AsyncDataStore.kt
└── commonTest/kotlin/com/illiarb/peek/core/data/
    └── AsyncDataStoreTest.kt
```

### Test Dependencies

- **Kotlin Test**: `kotlin.test.*` for assertions
- **Turbine**: For testing Flows (`app.cash.turbine`)
- **Coroutines Test**: `kotlinx.coroutines.test.runTest`

### Test naming

1. Use backtick-quoted descriptive names: `` `it should do X when Y` ``

### Stubs (test doubles)

- Prefer test double implementation over mocks
- Name doubles classes with Stub prefix
- If a method returns values, provide it via lambda that can be replaced from a test

```kotlin
interface ValueProvider {
  fun provideValue(param: Int): Value
}
class ValueProviderStub(
  var provideValue: (Int) -> Value = { TODO() },
) : ValueProvider {
  override fun provideValue(param: Int): Value = provideValue.invoke(param)
}
```

### Test contract rather than implementation details

Always strive to test as black-box as possible:

- Use real implementation instead of mocks or stubs whenever it fits: data classes, simple logic
- Avoid making methods or properties public, but test in a more integrated way

### Running Tests

```bash
# Run tests for a specific module
./gradlew :core:data:test -q
```

## CI

Project uses GitHub Actions which are stored in the .github directory.

## Gradle Conventions

### Running Gradle Commands

Always use the `-q` (quiet) flag when running Gradle commands to reduce output noise:

```bash
# Good
./gradlew build -q
./gradlew assembleDebug -q
./gradlew test -q

# Avoid (too verbose)
./gradlew build
```

### Linting and Static Analysis

Use Detekt to verify code changes and run lint checks:

```bash
./gradlew detekt -q
```

Run Detekt before committing to ensure code quality and catch issues early.

### Convention Plugins

Use convention plugins from `gradle/build-logic`:

```kotlin
plugins {
  alias(libs.plugins.peek.android.library)
  alias(libs.plugins.peek.multiplatform)
  alias(libs.plugins.peek.compose)
  alias(libs.plugins.metro)
}
```

### Dependencies

Use typesafe project accessors:

```kotlin
implementation(projects.core.data)
implementation(projects.uiKit.core)
```

Use version catalog for external dependencies:

```kotlin
implementation(libs.circuit.core)
implementation(libs.kotlinx.collections)
```
