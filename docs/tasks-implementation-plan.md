# Tasks Feature - Implementation Plan

## Overview
A **minimalistic and fast** unified tasks feature that combines:
- **Regular tasks**: One-time tasks for the day (to-do list)
- **Habits**: Recurring daily tasks marked with a "habit" flag that appear every day

Users can:
- Create tasks for the day - **with minimal clicks** (2-3 taps)
- Mark certain tasks as **habits** so they automatically appear every day
- Check off tasks/habits - **single tap to complete**
- View habit history by filtering completed tasks by the habit flag
- Optionally assign tasks to time of day (morning, midday, evening) or leave as "Anytime"

**Key Insight**: Habits are simply daily recurring tasks. Instead of two separate features, we have one unified task system where:
- Regular tasks = one-time, disappear after completion or the day ends
- Habits = tasks marked with `isHabit = true`, reappear every day, completion history is tracked

**Core UX Principles:**
- **Speed first**: Every interaction should be as fast as possible
- **Minimal clicks**: Create task in 2-3 taps max, complete in 1 tap
- **No unnecessary UI**: Clean, focused interface with only essential elements
- **Immediate feedback**: Visual confirmation for all actions without delays
- **Zero friction**: No confirmation dialogs, no extra steps, no unnecessary navigation

## Architecture
Following the same patterns as the `summarizer` feature module:
- Separate Gradle module: `features:tasks`
- SQLDelight database for local storage
- Circuit framework for UI and navigation
- Metro for dependency injection
- Kotlin Multiplatform (Android + iOS)

**Design System:**
- Leverage existing UI components from `ui-kit` module
- Any new reusable UI components should be added to `ui-kit`, not the feature module
- **Important**: Components in `ui-kit` must be generic and not tied to specific features
  - ✅ Good: `CheckableListItem`, `SwipeToDeleteContainer`, `SectionHeader`
  - ❌ Bad: `TaskListItem`, `HabitCheckbox`, `TaskSectionHeader`
- Feature-specific compositions of generic components stay in the feature module

**Minimalistic Design Approach:**
- **Single screen architecture**: No navigation between screens for core tasks
- **Bottom sheets for input**: Add/edit via bottom sheets for focused experience
- **Optimistic updates**: UI updates immediately, database syncs in background
- **Zero-wait operations**: All local operations are synchronous or near-instant
- **Minimal state**: Only essential state, no complex state machines
- **Direct manipulation**: Tap to toggle, swipe to delete, no intermediate steps

## Module Structure

```
features/tasks/
├── build.gradle.kts
└── src/
    ├── commonMain/
    │   ├── kotlin/com/illiarb/peek/features/tasks/
    │   │   ├── TasksService.kt                # Public service interface
    │   │   ├── DefaultTasksService.kt         # Service implementation
    │   │   ├── domain/
    │   │   │   └── Task.kt                    # Domain model: Task entity (includes completion state)
    │   │   ├── db/
    │   │   │   └── TasksDao.kt                # Database access layer
    │   │   ├── repository/
    │   │   │   └── TasksRepository.kt         # Repository for data operations
    │   │   ├── di/
    │   │   │   ├── TasksBindings.kt           # DI bindings container
    │   │   │   └── TasksPlatformBindings.kt   # Platform-specific DI (expect)
    │   │   └── ui/
    │   │       ├── TasksScreen.kt             # Compose UI
    │   │       ├── TasksScreenContract.kt     # State/Event contracts + Component
    │   │       └── TasksScreenPresenter.kt    # Presenter logic
    │   └── sqldelight/com/illiarb/peek/features/tasks/
    │       └── tasks.sq                       # SQLDelight schema
    ├── androidMain/kotlin/com/illiarb/peek/features/tasks/di/
    │   └── TasksPlatformBindings.android.kt   # Android SQLDriver provider
    └── iosMain/kotlin/com/illiarb/peek/features/tasks/di/
        └── TasksPlatformBindings.ios.kt       # iOS SQLDriver provider
```

## Database Schema (SQLDelight)

### Tables

1. **tasks** table:
   - `id` (TEXT PRIMARY KEY) - UUID string
   - `title` (TEXT NOT NULL) - Task name/description
   - `is_habit` (INTEGER NOT NULL) - Boolean (0/1): if true, task is a recurring habit
   - `time_of_day` (TEXT) - Nullable enum: "MORNING", "MIDDAY", "EVENING", or NULL for "Anytime"
   - `created_at` (INTEGER NOT NULL) - Timestamp (epoch milliseconds)
   - `created_for_date` (TEXT) - Date string (YYYY-MM-DD): for regular tasks, the date they were created for; NULL for habits
   - `archived` (INTEGER NOT NULL) - Boolean (0/1) for soft delete

2. **task_completions** table (internal, not exposed as domain model):
   - `task_id` (TEXT NOT NULL) - Foreign key to tasks.id
   - `date` (TEXT NOT NULL) - Date string in ISO format (YYYY-MM-DD)
   - `completed_at` (INTEGER NOT NULL) - Timestamp (epoch milliseconds)
   - PRIMARY KEY(task_id, date) - One completion per task per day

### Queries

**Task Management:**
- `allActiveTasks` - Get all non-archived tasks
- `allHabits` - Get all tasks where is_habit = 1
- `taskById` - Get single task by ID
- `insertTask` - Insert new task
- `archiveTask` - Soft delete task (set archived = 1)

**Tasks with Completion Status (main query for UI):**
- `tasksForDateWithCompletion` - Get tasks for a date with completion status joined
  - Returns: regular tasks for that date + all habits, with `is_completed` boolean
  - Uses LEFT JOIN on task_completions to check if completed for given date

**Completion Tracking (internal):**
- `insertCompletion` - Insert completion record (task_id, date)
- `deleteCompletion` - Delete completion record
- `isTaskCompletedForDate` - Check if specific task is completed for a date
- `completedDatesForTask` - Get all dates a task was completed (for habit history)
- `completedHabitsForDate` - Get all habits that were completed on a specific date (for history view)
- `completedHabitsInRange` - Get all habit completions in date range, joined with task data (for history view)

## Domain Models

### Task
```kotlin
data class Task(
    val id: String,                    // UUID
    val title: String,                 // User-provided name
    val isHabit: Boolean,              // If true, this is a recurring daily habit
    val timeOfDay: TimeOfDay?,         // Optional: MORNING, MIDDAY, EVENING, or null for "Anytime"
    val createdAt: Instant,            // Creation timestamp
    val createdForDate: LocalDate?,    // For regular tasks: the date; for habits: null
    val archived: Boolean = false      // Soft delete flag
)

enum class TimeOfDay {
    MORNING,
    MIDDAY,
    EVENING
}
```

**Task Types:**
- **Regular Task**: `isHabit = false`, `createdForDate = specific date`
  - Appears only on the date it was created for
  - Once completed or the day passes, it's done
- **Habit**: `isHabit = true`, `createdForDate = null`
  - Appears every day
  - Completion is tracked per day
  - History can be viewed by filtering completions

### Task (with completion state)
The `Task` domain model includes completion state for the current viewing date. No separate `TaskCompletion` domain model is needed - the completions table is only used internally to track which tasks are completed on which dates.

```kotlin
data class Task(
    val id: String,
    val title: String,
    val isHabit: Boolean,
    val timeOfDay: TimeOfDay?,
    val createdAt: Instant,
    val createdForDate: LocalDate?,
    val archived: Boolean = false,
    val isCompleted: Boolean = false   // Completion state for current viewing date
)
```

### TaskDraft (for creating new tasks)
Used as input when creating a new task - keeps the service interface clean.

```kotlin
data class TaskDraft(
    val title: String,
    val isHabit: Boolean,
    val timeOfDay: TimeOfDay?,
    val forDate: LocalDate?            // null for habits, specific date for regular tasks
)
```

### DayHistory (for habit history view)
Data grouped by day for rendering the habit history list:

```kotlin
data class DayHistory(
    val date: LocalDate,
    val completedHabits: List<Task>,   // Habits that were completed on this day
    val totalHabits: Int,              // Total active habits for reference
)
```

This allows rendering a list like:
```
Jan 15 - 4/5 completed
  ✓ Morning workout
  ✓ Read 30 min
  ✓ Meditate
  ✓ Journal

Jan 14 - 3/5 completed
  ✓ Morning workout
  ✓ Read 30 min
  ✓ Meditate
```

## Service Interface

**Design Note**: All operations should be optimized for speed. Local database operations should feel instant. The UI always works with `Task` objects - no separate completion model is exposed. Uses `Async` wrapper for reactive data loading.

```kotlin
interface TasksService {
    // Main query for UI - returns tasks with completion state for the given date
    fun getTasksForDate(date: LocalDate): Flow<Async<List<Task>>>
        // Returns: regular tasks for that date + all active habits
        // Each task includes `isCompleted` state for the given date
        // Wrapped in Async for loading/error states
    
    // Task management
    suspend fun addTask(draft: TaskDraft): Result<Task>
        // Creates a new task from the draft
    
    suspend fun deleteTask(taskId: String): Result<Unit>
        // Soft delete - sets archived = true
    
    // Completion tracking (single tap = instant toggle)
    suspend fun toggleCompletion(taskId: String, date: LocalDate): Result<Boolean>
        // Returns new completion state immediately
        // Internally: inserts or deletes from task_completions table
    
    // Habit history (for history view, grouped by day)
    fun getHabitHistory(startDate: LocalDate, endDate: LocalDate): Flow<Async<List<DayHistory>>>
        // Returns list of days with completed habits for each day
        // Grouped by date, each day contains list of habits completed that day
        // Wrapped in Async for loading/error states
}
```

## UX Design Principles

### Speed & Minimalism
1. **Single-tap completion**: Tap task item/checkbox = instant toggle, no confirmation
2. **Quick add task**: 
   - FAB or "+" button opens bottom sheet with text field + options
   - Type name, optionally toggle "Habit", optionally select time period, tap save = done (2-3 taps)
   - Bottom sheets are allowed for a cleaner, focused experience
3. **No delete confirmation**: Swipe to delete with immediate removal
4. **Large touch targets**: Tasks should be easy to tap (minimum 48dp/44pt)
6. **Instant visual feedback**: Checkbox animates immediately, no loading states for local operations
7. **Smart defaults**: New tasks default to "Anytime" and regular task (not habit)

### Interaction Patterns
- **Tap task row** = toggle completion (1 tap)
- **Swipe left** = delete task (no confirmation)
- **Add task** = tap "+" → bottom sheet → type name → optionally toggle habit/time → save (2-3 taps)

### Bottom Sheets (Allowed)
Bottom action sheets are permitted for:
- Adding new tasks

Benefits of bottom sheets:
- Focused input experience
- Keyboard handling is cleaner
- Consistent with platform patterns
- Still fast (2-3 taps to complete actions)

## UI Screens

### Main Tasks Screen
**Design Philosophy**: Single screen, everything visible, zero navigation needed for core tasks

**State:**
- `tasks: Async<List<Task>>` - Today's tasks (regular tasks for today + all habits), each Task has isCompleted
- `showAddTaskSheet: Boolean` - Bottom sheet for adding task
- `eventSink: (Event) -> Unit`

**Events:**
- `TaskToggled(taskId: String)` - Single tap to complete/uncomplete
- `AddTaskClicked` - Opens add task bottom sheet
- `AddTaskDismissed` - Closes add task bottom sheet
- `AddTaskSubmitted(draft: TaskDraft)` - Saves and closes sheet
- `DeleteTask(taskId: String)` - Immediate delete, no confirmation
- `NavigateToHabitHistory` - Navigate to habit history screen

**UI Layout:**
```
┌─────────────────────────────┐
│ Tasks          [Habit History]│ ← Navigate to habit history screen
├─────────────────────────────┤
│ ☐ Task 1                    │
│ ☑ Task 2            🔄      │ ← 🔄 indicates habit
│ ☐ Task 3            🔄      │ ← Large tap target, single tap toggles
│ ☑ Task 4                    │
│ ☐ Task 5                    │
│ ☐ Task 6            🔄      │
├─────────────────────────────┤
│            [+ Add]          │ ← FAB or button opens bottom sheet
└─────────────────────────────┘
```

**Visual Distinction:**
- Habits show a small 🔄 (repeat) icon to indicate they're recurring
- Regular tasks have no icon
- Both are toggled the same way (single tap)

**Key UX Features:**
- **Large checkboxes**: Full row is tappable, checkbox on left (48dp minimum)
- **Bottom sheet for adding**: Clean, focused input experience
- **Simple list**: All today's tasks in one flat list (regular tasks + habits)
- **Empty states**: Show "+ Add your first task" with FAB
- **Habit History**: Separate screen (navigate via top bar button)
- **Haptic feedback**: Subtle vibration on toggle for tactile confirmation

### Add Task Flow (2-3 taps)
1. Tap FAB or "+" button
2. Bottom sheet opens with:
   - Text field (auto-focused, keyboard appears)
   - "Repeat daily" toggle/switch (off by default = regular task, on = habit)
   - Optional time period chips: "Anytime" (default), "Morning", "Midday", "Evening"
   - Save button (or auto-save on enter)
3. Type task name → optionally toggle "Repeat daily" → optionally tap time period → tap Save

```
┌─────────────────────────────┐
│ Add Task               [×]  │
├─────────────────────────────┤
│ ┌─────────────────────────┐ │
│ │ Task name...           │ │ ← Auto-focused text field
│ └─────────────────────────┘ │
│                             │
│ [○ Repeat daily (Habit)]    │ ← Toggle switch, off by default
│                             │
│ Time (optional):            │
│ [Anytime✓] [Morning] [Midday] [Evening] │ ← Chips
│                             │
│              [Save]         │ ← Primary action button
└─────────────────────────────┘
```

### Delete Task Flow (1 gesture)
- Swipe left on task = immediate delete (no confirmation)

### Habit History Screen (Separate Screen)
**Purpose**: View completion history for habits only (filter tasks by is_habit = true)

**State:**
- `history: Async<List<DayHistory>>` - Days with completed habits, grouped by date
- `eventSink: (Event) -> Unit`

**Events:**
- `NavigateBack` - Go back to tasks screen

**UI:**
- Full screen with back navigation
- List grouped by day, each day shows:
  - Header: "Jan 15 - 4/5 completed"
  - List of completed habit names below

**Design:**
```
┌─────────────────────────────┐
│ ← Habit History             │
├─────────────────────────────┤
│ Jan 15 - 4/5 completed      │
│   ✓ Morning workout         │
│   ✓ Read 30 min             │
│   ✓ Meditate                │
│   ✓ Journal                 │
├─────────────────────────────┤
│ Jan 14 - 3/5 completed      │
│   ✓ Morning workout         │
│   ✓ Read 30 min             │
│   ✓ Meditate                │
├─────────────────────────────┤
│ Jan 13 - 5/5 completed      │
│   ...                       │
└─────────────────────────────┘
```

- Data comes from `getHabitHistory()` service method
- Shows only habit completions, not regular tasks

## Navigation Integration

### Screen Definition
Add to `features/navigation-map/src/commonMain/kotlin/com/illiarb/peek/features/navigation/map/Screens.kt`:

```kotlin
@CommonParcelize
@Immutable
public data object TasksScreen : Screen, CommonParcelable
```

### Entry Point: Home Screen Top App Bar

The tasks feature is accessible from the home screen's top app bar, alongside Bookmarks and Settings icons.

**Integration Steps:**

1. **Add Event to HomeScreenContract**:
   ```kotlin
   sealed interface Event : CircuitUiEvent {
     // ... existing events
     data object TasksClicked : Event
   }
   ```

2. **Add Icon Button to TopBarActions**:
   ```kotlin
   @Composable
   private fun TopBarActions(eventSink: (Event) -> Unit) {
     // Order: Tasks → Bookmarks → Settings (left to right)
     IconButton(onClick = { eventSink.invoke(Event.TasksClicked) }) {
       Icon(
         imageVector = Icons.Filled.CheckCircle,
         contentDescription = stringResource(Res.string.acsb_action_tasks),
       )
     }
     // ... rest of icons
   }
   ```

3. **Handle Navigation in HomeScreenPresenter**:
   ```kotlin
   is Event.TasksClicked -> {
     navigator.goTo(TasksScreen)
   }
   ```

## Dependency Injection

### Module Dependencies
- `libs.sqldelight.android` / `libs.sqldelight.native`
- `libs.sqldelight.coroutines`
- `libs.sqldelight.primitive`
- `libs.circuit.core`
- `libs.kotlinx.datetime`
- `libs.kotlin.coroutines.core`
- `projects.core.arch`
- `projects.core.coroutines`
- `projects.core.data`
- `projects.core.logging`
- `projects.core.types`
- `projects.features.navigationMap`
- `projects.uiKit.core`
- `projects.uiKit.resources`

### DI Bindings Structure
- `TasksBindings` - Main binding container
- `TasksBindingsInternal` - Internal bindings (DAO, Repository, Service)
- `TasksPlatformBindings` - Platform-specific SQLDriver provider (expect/actual)

## Implementation Steps

### Phase 1: Foundation
1. Create `features/tasks` module ✅
2. Add `build.gradle.kts` with SQLDelight, Circuit, and other dependencies
3. Update `settings.gradle.kts` to include new module ✅
4. Update `app/build.gradle.kts` to include new module dependency ✅
5. Create SQLDelight schema (`tasks.sq`)
6. Create domain model (`Task.kt` with completion state, `DaySummary.kt` for history)

### Phase 2: Data Layer
7. Create `TasksDao.kt` with database operations
8. Create `TasksRepository.kt` with business logic
9. Create `TasksService.kt` interface and implementation
10. Set up DI bindings (`TasksBindings.kt`)
11. Create platform-specific SQLDriver providers

### Phase 3: UI Layer (Prioritize Speed & Minimalism)
12. Create `TasksScreenContract.kt` with State/Event definitions
    - Bottom sheet states for add/edit
    - Minimize state complexity
13. Create `TasksScreenPresenter.kt` with state management logic
    - Optimize for instant feedback (no async delays for local operations)
    - Handle bottom sheet show/hide
14. Create `TasksScreen.kt` Compose UI
    - **Single screen design** - no navigation for core tasks
    - Large touch targets (minimum 48dp)
    - Sections: Anytime, Morning, Midday, Evening (hide empty sections)
    - Visual distinction for habits (🔄 icon)
    - Instant checkbox toggle animations
    - Swipe gesture for delete
15. Create Add Task bottom sheet component
    - Text field with auto-focus
    - "Repeat daily" toggle for habit
    - Optional time period selector (chips)
    - Save button
16. Create Habit History screen (separate screen with navigation)
17. Add haptic feedback for interactions
18. Optimize animations for speed (fast, not fancy)

### Phase 4: Navigation & Integration
19. Add `TasksScreen` to navigation map (`Screens.kt`) ✅
20. Register UI and Presenter factories in DI
21. **Entry point already added to Home screen top app bar** ✅
22. Test end-to-end flow (navigate from home → tasks → create → complete)

### Phase 5: Polish & Performance
23. Add empty states (inline, not separate screens)
24. **Skip loading states** for local operations (they should be instant)
25. Add error handling (minimal, non-intrusive)
26. Optimize animations for speed (fast transitions, not decorative)
27. Add accessibility support (large touch targets help here)
28. Add haptic feedback for all interactions

## Database Migration Strategy

Since this is a new feature, start with version 1 of the database schema. Future migrations can be handled by SQLDelight's migration system.

## Testing Considerations

- Unit tests for Repository layer
- Unit tests for Presenter logic
- Integration tests for database operations
- UI tests for critical user flows
- **UX flow tests**: Verify minimal click counts (create task ≤3 taps, complete ≤1 tap)

## Future Enhancements (Out of Scope for MVP)

### Core Features
- Streak tracking for habits
- Task/habit statistics and charts
- Task templates
- Export data
- Task categories/tags (beyond time of day)
- Weekly recurring patterns (not just daily)
- Due dates and reminders for regular tasks
- Task priority levels

### Notifications
**Time-based notifications for incomplete tasks/habits**

- Send notifications during the designated time period (morning, midday, evening) if there are active tasks that haven't been completed yet
- Allow users to configure notification times for each time period
- Notification should show:
  - List of incomplete tasks for that time period
  - Quick action to mark tasks as complete from notification
  - Dismiss if all tasks are already completed
- Implementation considerations:
  - Use `core:work-scheduler` module
  - Schedule daily work requests for each time period
  - Check task completion status before sending notification
  - Handle notification permissions (Android/iOS)
  - Allow users to enable/disable notifications per time period

### Overlay Widget / Floating Action Button
**System UI overlay for quick task completion**

- Floating widget/bubble that can be drawn over other apps and system UI
- Quick access to mark tasks as complete without opening the app
- Features:
  - Compact view showing incomplete tasks for current time period
  - Tap to toggle task completion
  - Expandable to show all tasks for the day
  - Draggable position on screen
  - Auto-hide when all tasks for current period are completed
- Implementation considerations:
  - **Android**: Use `TYPE_APPLICATION_OVERLAY` window type with `WindowManager`
  - **iOS**: Use custom overlay view
  - Requires overlay/draw over other apps permission
  - Battery optimization considerations
