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

## Architecture & Patterns

### MVVM (Model-View-ViewModel)

The app uses the MVVM pattern to separate UI logic from business logic:

- **ViewModel**: Handles game state, board logic, and timer. Exposes state via `StateFlow` for reactive UI updates.
- **View (Compose Screens)**: Observes ViewModel state and renders UI. User actions are sent to ViewModel methods.
- **Model**: Board engine, data entities, and repository.

### Dependency Injection (Hilt)

Hilt is used for dependency injection, providing ViewModels, repositories, and other components. This improves testability and modularity.

### Repository Pattern

`ResultsRepository` abstracts data access for game results, allowing easy swapping of data sources and simplifying testing.

### Unidirectional Data Flow

State flows from ViewModel to UI, and user events flow back to ViewModel. This makes state management predictable and easier to debug.

### Compose UI Components

- **Composable functions**: UI is built from small, reusable composables (e.g., `GameCard`, `BoardView`, `WinDialog`).
- **Preview Providers**: Used for rapid UI prototyping and testing.

### Navigation

Navigation is handled via Jetpack Compose Navigation, with type-safe routes and animated transitions.

## Reasoning Behind Choices

- **MVVM**: Clean separation of concerns, easier testing, and reactive UI updates.
- **Hilt DI**: Reduces boilerplate, improves scalability, and enables constructor injection for ViewModels.
- **Repository Pattern**: Future-proofs data layer, supports caching or remote sources.
- **Compose**: Modern, declarative UI toolkit for Android, enables rapid development and better performance.
- **Unidirectional Data Flow**: Prevents state bugs and makes UI predictable.

## Code generation and AI

AI code generation used only for finding solutions or generating boilerplate code, auto-completion, this README file and narration for video :) 

## Testing

Unit tests are provided for core ViewModel logic, ensuring correctness of game rules and state transitions.

## License

MIT
