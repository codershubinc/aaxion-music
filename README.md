# Aaxion Music 🎵

A premium, purely native Android client for the ![Aaxion](https://github.com/codershubinc/aaxion) self-hosted media and music server. Built with 100% modern Android architecture, prioritizing performance, an AMOLED-first aesthetic, and seamless local network discovery.

## ✨ Features

* **Zero-Config Server Discovery:** Uses Android's Network Service Discovery (NSD) to automatically find your local Aaxion Go server on the network.
* **AMOLED Pitch-Black UI:** Designed strictly with Material 3 using a pure `#000000` background to save battery and provide a premium, Spotify-like feel.
* **Responsive Layout:** Adapts flawlessly between mobile (Navigation Drawer) and tablet/desktop (Persistent Sidebar) using Jetpack Compose Window Size Classes.
* **Floating Music Island:** A persistent, animated mini-player that hovers above the UI when a track is playing.
* **Premium Full-Screen Player:** Features real-time blurred album art backgrounds, queue management, and high-fidelity scrubbers.
* **Background Playback:** Utilizes AndroidX Media3 (`MediaSessionService`) to ensure music continues playing in the background with native lock screen controls.

## 🛠️ Tech Stack

* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose (100% XML-free)
* **Media Engine:** AndroidX Media3 (ExoPlayer + MediaSession)
* **Image Loading:** Coil 3 (with OkHttp Network Fetcher)
* **Networking:** Kotlin Coroutines & `HttpURLConnection`
* **Architecture:** State Hoisting, CompositionLocals (`LocalMusicController`), and Material 3 Design System.

## 🚀 Getting Started

### Prerequisites
* Android Studio Ladybug (or newer).
* Minimum SDK: API 24 (Android 7.0).
* Target SDK: API 36.
* The **Aaxion Go Backend Server** must be running on your local network and broadcasting via mDNS (`_aaxion._tcp.`).

### Installation
1.  Clone the repository:
    ```bash
    git clone [https://github.com/codershubinc/aaxion-music.git](https://github.com/codershubinc/aaxion-music.git)
    ```
2.  Open the project in Android Studio.
3.  Allow Gradle to sync and download the required dependencies (Media3, Coil, Compose BOM).
4.  Run the app on an emulator or physical device connected to the **same Wi-Fi network** as your Aaxion server.

## 📡 Backend API Integration

This client expects the Aaxion Go backend to expose the following endpoints:
* `POST /auth/login` - Returns a JWT token.
* `GET /music/all` - Returns an array of `MusicTrack` JSON objects.
* `GET /music/stream?id={id}&tkn={token}` - The raw audio stream.
* `GET /files/thumbnail?path={path}&tkn={token}` - Heavily optimized JPEG/PNG album art.
* `GET /files/view-image?path={path}&tkn={token}` - High-resolution album art for the full-screen player.

## 🤝 Contributing

Contributions are welcome! If you'd like to improve the UI, add caching, or optimize the ExoPlayer buffering strategies, please open a PR.

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
