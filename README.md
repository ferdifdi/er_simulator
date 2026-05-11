<div align="center">

# 🏥 ER Triage Simulator

### A Real-Time Emergency Room Triage System

*Multithreaded patient flow · Priority-based queuing · Live WebSocket updates*

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![H2 Database](https://img.shields.io/badge/H2-In_Memory-0000BB?style=for-the-badge&logo=databricks&logoColor=white)](https://www.h2database.com/)

---

**Simulates an Emergency Room environment where patients arrive randomly, are triaged by severity (ESI levels), assigned to treatment rooms, and discharged — all in real time.**

[Getting Started](#-getting-started) · [API Reference](#-api-reference) · [Architecture](#-architecture) · [Authors](#-authors)

</div>

---

## ✨ Features

🔴 **5-Level ESI Triage** — Patients are classified from ESI-1 (critical) to ESI-5 (non-urgent), with automatic priority queuing so the sickest are seen first.

⚡ **Concurrent Treatment Rooms** — Multiple rooms run on separate threads, each with an assigned doctor who treats patients in parallel — just like a real ER.

📡 **Live WebSocket Updates** — Queue changes, room status, and triage events are pushed to connected clients in real time via STOMP over WebSocket.

🖥️ **Dual Interface** — A web-based dashboard served at `localhost:8080` and an optional Swing GUI launched with the `--gui` flag.

🧪 **Comprehensive Test Suite** — Unit tests, integration tests, and a 50-thread concurrent stress test to ensure stability under load.

📊 **Audit Trail** — Every admission, assignment, discharge, and upgrade is logged as a `TriageEventLog` with timestamps and wait-time tracking.

---

## 🚀 Getting Started

### Prerequisites

| Tool | Version |
|------|---------|
| Java JDK | 17+ |
| Maven | 3.9+ |

### Installation

```bash
# 1. Clone the repository
git clone https://github.com/ferdifdi/er_simulator.git
cd er_simulator

# 2. Build the project
mvn clean install

# 3. Run the simulator
mvn spring-boot:run
```

The web dashboard will be live at **http://localhost:8080**

### Optional: Launch with Swing GUI

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--gui"
```

### Run Tests

```bash
mvn clean test
```

---

## 🔌 API Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/patients` | Admit a new patient |
| `GET` | `/queue` | View current waiting queue |
| `GET` | `/rooms` | View treatment room statuses |
| `GET` | `/report` | Get aggregate statistics |
| `PUT` | `/patients/{id}/level` | Upgrade a patient's triage level |
| `DELETE` | `/patients/{id}` | Discharge a patient |

### Quick Examples

```bash
# Admit a patient
curl -X POST http://localhost:8080/patients \
  -H "Content-Type: application/json" \
  -d '{"name": "Jane Doe", "age": 34, "triageLevel": "ESI_2"}'

# Check the queue
curl http://localhost:8080/queue

# View room statuses
curl http://localhost:8080/rooms

# Get report
curl http://localhost:8080/report
```

---

## 🏗 Architecture

```
er_simulator/
├── src/main/java/com/ersim/
│   ├── ErSimulatorApplication.java        # Entry point + optional GUI launcher
│   │
│   ├── model/
│   │   ├── Patient.java                   # JPA entity with priority ordering
│   │   ├── Doctor.java                    # In-memory doctor with treat() simulation
│   │   ├── TriageEventLog.java            # Audit log entity with wait tracking
│   │   └── enums/                         # ESI levels, statuses, event types
│   │
│   ├── concurrent/
│   │   ├── TriageQueue.java               # Thread-safe priority queue
│   │   ├── TreatmentRoom.java             # Runnable room thread with doctor
│   │   └── PatientArrivalThread.java      # Random patient generator thread
│   │
│   ├── service/
│   │   └── TriageService.java             # Core business logic orchestrator
│   │
│   ├── controller/
│   │   └── TriageController.java          # REST API endpoints
│   │
│   ├── repository/
│   │   ├── PatientRepository.java         # JPA patient queries
│   │   └── TriageEventLogRepository.java  # JPA event log queries
│   │
│   ├── websocket/
│   │   ├── WebSocketBroadcaster.java      # Push updates to /topic/*
│   │   └── WebSocketConfig.java           # STOMP + SockJS configuration
│   │
│   └── gui/
│       └── DashboardFrame.java            # Swing dashboard with live refresh
│
├── src/main/resources/
│   ├── application.properties             # Server, DB, and simulator config
│   └── static/index.html                  # Web-based dashboard
│
└── src/test/java/com/ersim/              # Unit + integration + stress tests
```

### How It Works

```
Patient Arrives → TriageQueue (priority sorted by ESI level)
                        ↓
              TriageService assigns → TreatmentRoom (thread)
                                          ↓
                                    Doctor.treat() (simulated)
                                          ↓
                                    Patient Discharged
                                          ↓
                              TriageEventLog recorded
                              WebSocket broadcast sent
```

### Key Design Decisions

- **Thread-safe queue** using `PriorityBlockingQueue` ensures ESI-1 patients are always dequeued first, even under concurrent access.
- **Each treatment room is its own thread**, allowing true parallel patient treatment.
- **H2 in-memory database** keeps setup frictionless — no external DB needed.
- **WebSocket + REST dual access** supports both real-time dashboards and programmatic integration.

---

## ⚙️ Configuration

Edit `src/main/resources/application.properties`:

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | `8080` | Server port |
| `ersim.rooms.count` | `4` | Number of treatment rooms |
| `ersim.arrival.intervalMs` | `2000` | Base interval for auto-generated patients |
| `spring.h2.console.enabled` | `true` | Enable H2 console at `/h2-console` |

---

## 🧪 Testing

The test suite covers:

- **Model tests** — Patient priority ordering, Doctor treatment timing
- **Repository tests** — JPA queries for patients and event logs
- **Service tests** — Full integration with concurrent admit stress test (50 threads)
- **Controller tests** — MockMvc tests for all REST endpoints
- **Concurrency tests** — PatientArrivalThread lifecycle, WebSocket broadcasting

```bash
mvn clean test
```

---

## 👥 Authors

| | Name | Focus Area |
|---|------|-----------|
| 🛠️ | **Ferdi** ([@ferdifdi](https://github.com/ferdifdi)) | Patient model, TriageQueue, TreatmentRoom, TriageService, REST controller, repositories, frontend |
| 🎨 | **Sruthi** ([@Sruthi-2002](https://github.com/Sruthi-2002)) | Doctor model, TriageEventLog, enums, PatientArrivalThread, WebSocket layer, Swing GUI, tests |

---

## 📜 License

This project was built as an academic exercise for coursework.

---

<div align="center">

*Built with ☕ Java, 🍃 Spring Boot, and a lot of thread-safety paranoia.*

</div>
