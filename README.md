<div align="center">
  <img src="https://raw.githubusercontent.com/purvanshjoshi/Price-Tracker/master/extension/icons/icon128.png" alt="Price Scout Logo" width="120" height="120">
  <h1>🚀 Price Scout</h1>
  <p><strong>The Intelligent Real-Time Price Discovery Engine</strong></p>
  <p><i>Stop chasing deals. Let them come to you with a multithreaded Core Java backend and a premium Chrome Extension experience.</i></p>

  <p>
    <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17">
    <img src="https://img.shields.io/badge/Chrome-Extension-4285F4?style=for-the-badge&logo=google-chrome&logoColor=white" alt="Chrome Extension">
    <img src="https://img.shields.io/badge/SQLite-Database-003B57?style=for-the-badge&logo=sqlite&logoColor=white" alt="SQLite">
    <img src="https://img.shields.io/badge/Jsoup-Scraper-FF9900?style=for-the-badge&logo=java&logoColor=white" alt="Jsoup">
  </p>
</div>

<br />

## 🌟 The Vision
In an era of dynamic pricing and "Big Billion" sales, manual price checking is a waste of time. **Price Scout** is a performance-first tool designed by **The Avengers** to deliver real-time product data directly from the source. No more old, cached prices—just the absolute latest truth from Amazon and Flipkart, delivered in milliseconds.

## 🎯 Why Price Scout?
Traditional trackers often show prices that are hours or even days old. **Price Scout** is different:
- **Zero Latency:** We don't use a "middleman" API. The scraping happens locally on your machine.
- **Privacy First:** Your search data stays in your local SQLite database, not on our servers.
- **Pure Performance:** Multi-threaded execution means we check multiple stores at the exact same time.

---

## ⚡ Key Highlights
- **💨 Concurrent Engine:** Powered by Java `ExecutorService`, our engine spawns multiple workers to race for the best price.
- **🔍 4-Byte Native Protocol:** Uses the high-speed **Chrome Native Messaging** bridge to communicate between Java and JS.
- **📦 Zero-Configuration SQL:** Leverages SQLite for a portable, file-based history that requires no server setup.
- **🎭 Intelligent Selectors:** Advanced Jsoup implementation that adapts to e-commerce site structures.
- **📊 Trend Awareness:** Logs every lookup with precision timestamps to help you identify price drops over time.

---

## 🏗️ System Architecture

```mermaid
graph TD
    A[Browser Popup UI] <-->|Native Messaging Bridge| B(Java Host Launcher)
    B <-->|System.in / Out| C{Price Scout Engine}
    
    C -->|Dispatch| D[Thread Pool: 3 Workers]
    
    D -->|Worker 1| E[Amazon Scraper: Jsoup]
    D -->|Worker 2| F[Flipkart Scraper: Jsoup]
    D -->|Worker 3| G[Future Scrapers...]
    
    E & F & G -->|Results| H[Cheapest Price Selector]
    H -->|Save Trend| I[(SQLite DB: price_history)]
    H -->|Return JSON| B
```

---

---

## 🛠️ The Core Technology Stack

| Layer | Tools & Technologies |
| :--- | :--- |
| **Backend Core** | <img src="https://img.shields.io/badge/Java-17-orange?logo=java"> <img src="https://img.shields.io/badge/Maven-3.9-C71A36?logo=apache-maven"> |
| **Parsing Engine** | <img src="https://img.shields.io/badge/Jsoup-1.17-yellow"> (HTML DOM / CSS Selectors) |
| **Concurrency** | `java.util.concurrent` (ExecutorService, Async Future) |
| **Database** | <img src="https://img.shields.io/badge/SQLite-3-003B57?logo=sqlite"> (Zero-Configuration Persistent Storage) |
| **Messaging** | **Chrome Native Messaging** (4-Byte Byte-Stream Protocol) |
| **Frontend UI** | <img src="https://img.shields.io/badge/HTML5-E34F26?logo=html5"> <img src="https://img.shields.io/badge/CSS3-1572B6?logo=css3"> <img src="https://img.shields.io/badge/JavaScript-F7DF1E?logo=javascript"> |

---

## 🚀 Deployment & Installation
Running **Price Scout** requires a quick one-time handshake between your browser and your machine.

### 📋 Prerequisites
- **JDK 17+** & **Maven** installed.
- **Google Chrome** browser.

### 🛠️ Setup in 3 Quick Steps
For a complete walkthrough, see the [Detailed Setup Guide](docs/setup_guide.md).

1. **Build the Engine:** Run `mvn clean package` inside the `backend` folder.
2. **Register Host:** Link your registry via the `host-config/` scripts.
3. **Install Extension:** Load the `extension/` folder in Chrome Developer Mode.

---

## 🦸‍♂️ The Avengers (Engineered by)

<div align="center">

| Hero | Role | Focus | GitHub |
| :--- | :--- | :--- | :--- |
| 🛡️ **Purvansh Joshi** | **Architect** | UI/UX & Native Messaging | [@PurvanshJoshi](https://github.com/PurvanshJoshi) |
| ⚡ **Parth Nailwal** | **Backend Lead** | Multithreading & Logic | [@parthnailwal](https://github.com/parthnailwal) |
| 🏹 **Vansh Singh** | **Data Lead** | Scrapers & SQLite | [@vanshsingh](https://github.com/vanshsingh) |

</div>

<br />

<div align="center">
  <p><i>"The power of Java, the reach of the Browser."</i></p>
  <img src="https://img.shields.io/badge/Status-Complete-green?style=for-the-badge" alt="Status">
</div>

