# Contributing to Aaxion Music 🎵

First off, thank you for considering contributing to Aaxion Music! It's people like you that make self-hosted, open-source tools great. 

Aaxion Music is built with a very specific vision: to provide a premium, lightning-fast, native client for the Aaxion media server. To maintain this standard, we have strict architectural and design rules. Please read this guide carefully before submitting a Pull Request.

## 📐 The Aaxion Architecture Rules (Strictly Enforced)

Before writing any code, please ensure your contribution adheres to the following core pillars of the project:

### 1. 100% Jetpack Compose (No XML)
* We do not use XML for layouts, menus, or navigation. 
* All UI must be written declaratively using Jetpack Compose.
* *Exception:* Standard Android configuration files (`AndroidManifest.xml`, vector drawables, or legacy launcher icons).

### 2. The AMOLED "Pitch-Black" Aesthetic
Aaxion is designed for OLED screens and dark mode by default.
* **Backgrounds:** The absolute root of screens must be pure black (`Color(0xFF000000)`).
* **Surfaces:** Floating elements, sidebars, and dialogs should use dark zinc tones (e.g., `Color(0xFF18181B)` or `Color(0xFF27272A)`).
* **Accents:** Use sharp, modern accents (like Cyan `Color(0xFF00E5FF)`) sparingly for active states.
* Do not introduce bright/light mode themes or legacy Material 2 default colors (like purple `#BB86FC`).

### 3. Media3 Engine Only
* Audio playback and background services must exclusively use **AndroidX Media3** (`MediaSessionService`, `ExoPlayer`).
* Do not use deprecated classes like `MediaPlayer`, `MediaSessionCompat`, or `MediaBrowserServiceCompat`.

### 4. Efficient State Management
* Use `items(key = { it.id })` in `LazyColumn` or `LazyRow` to ensure efficient DOM virtualization.
* Avoid heavy recompositions. Hoist state appropriately and use `remember` for complex calculations.

---

## 🛠️ Local Development Setup

To test your changes, you will need the Aaxion Go backend running on your local network.

1. **Android Studio:** Ensure you are using Android Studio Ladybug (or newer).
2. **Backend Server:** Run the Aaxion Go server locally. Ensure it is broadcasting via NSD (Network Service Discovery) or note its IP address.
3. **Network:** Connect your Android device/emulator to the same Wi-Fi network as the Go server.
4. **Build:** Sync the Gradle project and run it.

---

## 🚀 How to Contribute

### Reporting Bugs
If you find a bug, please open an issue with the following information:
* Your Android device model and OS version.
* Aaxion app version.
* Steps to reproduce the bug.
* Expected behavior vs. actual behavior.
* Crash logs (Logcat) if applicable.

### Suggesting Enhancements
We welcome ideas for new features! Open an issue and use the "Feature Request" label. Explain:
* Why this feature is needed.
* How it fits into the "native, premium, lightweight" vision of Aaxion.
* Proposed implementation details (if you have them).

### Pull Request Process
1. **Fork** the repository and clone it locally.
2. **Create a branch** for your feature or bug fix (`git checkout -b feature/amazing-new-feature` or `bugfix/issue-123`).
3. **Write your code**, adhering to the architecture rules above.
4. **Test** your changes thoroughly with the Aaxion Go backend.
5. **Commit** your changes using clear, descriptive commit messages.
    * *Example:* `feat: add swipe-to-delete in queue Compose UI`
    * *Example:* `fix: resolve ExoPlayer crash on track skip`
6. **Push** to your fork and submit a **Pull Request** to the `main` branch.
7. Wait for a code review! We may ask for changes to align with the project's styling or performance standards.

## 📄 Licensing

By contributing to this repository, you agree that your contributions will be licensed under the [GNU Affero General Public License v3.0 (AGPL-3.0)](LICENSE) alongside the rest of the project.
