# nQueens

An Android app for solving the classic N-Queens puzzle. The goal is to place N queens on an NxN chessboard so that no two queens threaten each other.

## Features

- Interactive board for placing/removing queens
- Timer to track solution time
- Best times leaderboard per board size
- Board size selection (4x4 to 20x20)
- Modern UI with Jetpack Compose
- Dark/light mode support

[Video](nqueens_demo.mp4)

## Running the app

You can install the app on your connected device by running:

```
./gradlew :app:installDebug
```

## Running tests

To run tests from the terminal, execute:

```
./gradlew :app:testDebugUnitTest
```

## Architecture & Patterns

### Game Engine

Decided on the approach that is simple and easy to understand, rather than the most optimal one. The board maximum size is 20x20, so performance is was not a concern.
The board logic is encapsulated in the [BoardEngineImpl.kt](app/src/main/java/com/jarosz/szymon/nqueens/board/BoardEngineImpl.kt) class and NQueens game rules are implemented in [GameViewModel.kt](app/src/main/java/com/jarosz/szymon/nqueens/ui/game/GameViewModel.kt)

### MVVM

The app uses the MVVM pattern to separate UI logic from business logic:

- **ViewModel**: Handles game state, board logic, and timer. Exposes state via `StateFlow` for reactive UI updates.
- **View (Compose)**: Observes ViewModel state and renders UI. User actions are sent to ViewModel methods.
- **Model**: Board engine, data entities, and repository.

### Dependency Injection (Hilt)

Hilt is used for dependency injection, providing ViewModels, repository, and other components. This improves testability and modularity.

### Repository Pattern

`ResultsRepository` abstracts data access for game results, allowing easy swapping of data sources and simplifying testing.

### Unidirectional Data Flow

State flows from ViewModel to UI, and user events flow back to ViewModel. This makes state management predictable and easier to debug.

### Compose UI Components

- **Composable functions**: UI is built from small, reusable composables (e.g., `GameCard`, `BoardView`, `WinDialog`).
- **Preview Providers**: Used for rapid UI prototyping and testing.

### Navigation

Navigation is handled via Jetpack Compose Navigation.

## Reasoning Behind Choices

- **MVVM**: Clean separation of concerns, easier testing, and reactive UI updates.
- **Hilt DI**: Reduces boilerplate, improves scalability, and enables constructor injection for ViewModels.
- **Repository Pattern**: Future-proofs data layer, supports caching or remote sources.
- **Compose**: Modern, declarative UI toolkit for Android, enables rapid development and better performance.
- **Unidirectional Data Flow**: Prevents state bugs and makes UI predictable.

## Code generation and AI

AI tools and generation used mostly for finding solutions, generating boilerplate code, auto-completion, this README file and narration for video :) 

## Testing

Unit tests are provided for core ViewModel logic, ensuring correctness of game rules and state transitions.

## License

MIT
