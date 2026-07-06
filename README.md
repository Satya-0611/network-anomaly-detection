# Real-Time Network Anomaly Detection System

A distributed, machine-learning-powered pipeline designed to ingest, process, and classify network traffic in real-time to detect anomalous behavior and potential attack vectors.

## 🏗️ System Architecture

1. **Traffic Generator (Spring Boot / Java 17):** Simulates a high-throughput enterprise network environment, injecting weighted anomalous traffic (port scanning, massive payloads) to mimic hybrid workforce behaviors.
2. **Message Broker (Apache Kafka / Docker):** Acts as the highly available, fault-tolerant buffer, ingesting raw JSON logs from the Java microservice.
3. **AI Core (Python / FastAPI / Scikit-Learn):** A streaming analytics engine that buffers network logs, trains an **Isolation Forest** model on the fly, and evaluates incoming live traffic. 
4. **Operations Dashboard (React / Vite):** A real-time UI connected to the AI Core via **Server-Sent Events (SSE)**, instantly visualizing flagged threats with zero-polling overhead.

## 🚀 Key Technical Decisions

* **Why Kafka?** Decoupled the fast producer (Java) from the heavy consumer (Python ML), preventing memory crashes during traffic spikes.
* **Why Isolation Forest?** Chose over standard profiling models (like Autoencoders) because anomalies are "few and different," allowing decision trees to isolate threats with extremely low computational overhead ($O(n \log n)$ complexity), making it perfect for streaming data.
* **Why SSE over WebSockets?** Security dashboards require high-frequency, unidirectional data flow (Server -> Client). SSE provided native browser support and automatic reconnection without the heavy bidirectional handshake of WebSockets.

## 🛠️ Tech Stack
* **Backend:** Java 17, Spring Boot, Spring Scheduling
* **Streaming:** Apache Kafka, Docker
* **AI/ML:** Python 3, FastAPI, Scikit-Learn, Pandas
* **Frontend:** React 18, Vite, Server-Sent Events (SSE)


## ⚙️ Local Setup & Execution

### Prerequisites
* Java 17+ & Maven
* Python 3.10+
* Node.js 18+
* Docker Desktop (for Apache Kafka)

### 1. Boot the Message Broker
```bash
docker-compose up -d
(Ensure your docker-compose.yml configures Kafka on port 9092)

2. Start the AI Core (Python)
cd anomaly-detection-brain
python -m venv venv
# in powershell disable ExecutionPolicy security (Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass)
# Activate venv (Windows: .\venv\Scripts\activate | Mac/Linux: source venv/bin/activate)
pip install -r requirements.txt
uvicorn brain:app --reload
(Server will listen on port 8000)

3. Start the Traffic Generator (Java)
Open the root directory in your IDE and run NetworkAnomalyDetectionApplication.java to begin pumping simulated logs into Kafka.

4. Boot the Operations Dashboard (React)
cd react_ui
npm install
npm run dev
