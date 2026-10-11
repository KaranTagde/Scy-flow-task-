# SyncFlow — Real-Time Team Workflow & Project Synchronization

**SyncFlow** is a modern, offline-first Android application built with **Jetpack Compose** and **Material 3**. It enables software engineering teams to synchronize, automate, and orchestrate project workflows seamlessly across **GitHub**, **Jira**, **Slack**, and **n8n** automation webhooks.

---

## 🌟 Key Features

- **Multi-Platform Project Synchronization**:
  - **GitHub Integration**: Direct ingestion and caching of Pull Requests, Issues, and commit status checks.
  - **Jira Integration**: Project sprint tickets, story points, priority queues, and workflow transitions.
  - **Slack & n8n Automation**: Webhook triggers, cross-channel notification routing, and automated pipeline execution.

- **Offline-First Persistence with Room**:
  - Local database powered by **Android Room** with SQLite persistence.
  - Full local caching for:
    - `TaskEntity` (cross-platform synchronized work items)
    - `CachedGitHubItemEntity` (offline pull requests and repository issues)
    - `CachedJiraTicketEntity` (sprint tickets and agile board status)
  - Real-time offline indicators with live entity cache badges.

- **Automated Workflow Orchestrator**:
  - Rule-based triggers (e.g., *GitHub PR merged → Close Jira Ticket → Post Slack message*).
  - Execution history logs and pipeline health tracking.

- **AI-Powered Insights**:
  - Integrated sprint velocity analysis, blocker detection, and automated release note generation.

- **Modern Jetpack Compose UI**:
  - Strict Material 3 theming with dynamic color and dark mode support.
  - Fluid edge-to-edge layouts compliant with Android 15 WindowInsets.
  - Responsive cards, interactive status filters, search capabilities, and action sheets.

---

## 🛠️ Technology Stack & Architecture

- **Language**: Kotlin 2.0+
- **UI Toolkit**: Jetpack Compose (Material Design 3)
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Data Layer + StateFlow / Coroutines
- **Database**: Room Database with KSP (`TaskDao`, `CachedGitHubItemDao`, `CachedJiraTicketDao`)
- **Networking & Serialization**: Kotlinx Serialization
- **Testing**: Robolectric + JUnit 4 local JVM test suite

---

## 🚀 Building & Testing

### Prerequisites
- JDK 17 or JDK 21
- Android SDK (API Level 35 compileSdk, API Level 26 minSdk)
- Gradle 8.x

### Build Commands
To compile the debug APK:
```bash
gradle assembleDebug
```

To run unit and Robolectric tests:
```bash
gradle :app:testDebugUnitTest
```

---

## 📤 Publishing & Sharing from Google AI Studio

1. **Pushing to GitHub**:
   - In the top-right header or project settings menu of Google AI Studio, select **Export to GitHub** / **Push to GitHub**.
   - Authenticate your GitHub account and choose either a new repository or an existing one to push the complete codebase.

2. **Sharing the Web Preview**:
   - Use the **Share** button in the AI Studio interface to generate a shareable preview link.
   - Users can test and interact with the live application in the streaming Android emulator.

3. **Exporting as ZIP or APK**:
   - In the settings menu, select **Export Project (ZIP)** to download the full source code for local development in Android Studio.
   - Select **Download APK** or compile with `gradle assembleRelease` to distribute to testing teams.

---

## 📄 License
This project is licensed under the Apache 2.0 License.
