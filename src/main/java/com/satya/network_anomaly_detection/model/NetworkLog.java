package com.satya.network_anomaly_detection.model;

import java.time.Instant;

public record NetworkLog(
                Instant timestamp,
                String sourceIp,
                String destinationIp,
                String protocol,
                int port,
                long bytesSent,
                long bytesReceived,
                long durationMs) {
}