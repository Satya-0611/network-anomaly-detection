import json
import pandas as pd
import asyncio
from kafka import KafkaConsumer
from sklearn.ensemble import IsolationForest
from fastapi import FastAPI
from fastapi.responses import StreamingResponse
from fastapi.middleware.cors import CORSMiddleware
import warnings

warnings.filterwarnings("ignore")

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

model = IsolationForest(n_estimators=100, contamination=0.06, random_state=42)
training_data = []
is_trained = False

consumer = KafkaConsumer(
    'raw-network-logs',
    bootstrap_servers=['localhost:9092'],
    auto_offset_reset='latest',
    value_deserializer=lambda x: json.loads(x.decode('utf-8')),
    consumer_timeout_ms=1000
)

# SSE Generator
async def event_stream():
    global is_trained, training_data

    print("FastAPI Server listening to Kafka...")

    while True:
        records = consumer.poll(timeout_ms=1000)

        if not records:
            await asyncio.sleep(0.1)
            continue

        for topic_partition, messages in records.items():
            for message in messages:
                log_entry = message.value
                features = {
                    "bytesSent": log_entry["bytesSent"],
                    "bytesReceived": log_entry["bytesReceived"],
                    "durationMs": log_entry["durationMs"]
                }
            
                if not is_trained:
                    training_data.append(features)
                    if len(training_data) % 20 == 0:
                        print(f"Buffering: {len(training_data)}/200 logs...")

                    if len(training_data) >= 200:
                        print("Training Model...")
                        df = pd.DataFrame(training_data)
                        model.fit(df)
                        is_trained = True
                        print(f"Model Live! Waiting for anomalies to stream to UI")
                
                else:
                    # Prediction
                    df_current = pd.DataFrame([features])
                    Prediction = model.predict(df_current)[0]

                    if Prediction == -1:
                        alert_data = {
                            "type": "ANOMALY",
                            "port": log_entry["port"],
                            "targetIP": log_entry["destinationIp"],
                            "bytesSent": log_entry["bytesSent"],
                            "durationMs": log_entry["durationMs"]
                        }

                        print(f"FIRE: Streamed alert to React UI -> Port {log_entry["port"]}")

                        yield f"data: {json.dumps(alert_data)}\n\n"
        await asyncio.sleep(0.05)

@app.get("/stream")
async def stream_anomalies():
    return StreamingResponse(event_stream(), media_type="text/event/stream")