package com.satya.network_anomaly_detection.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.satya.network_anomaly_detection.model.NetworkLog;

import java.time.Instant;
import java.util.Random;

@Service
@Slf4j
public class TrafficGeneratorService {

    private final Random random = new Random();

    // Assuming normal request happens to Google, AWS, Azure, etc
    private final String[] NORMAL_DESTINATION_IPS = {
            "142.250.190.46", "13.250.177.223", "104.18.2.161", "52.223.40.100", "20.112.250.113"
    };

    // Runs every 500 milliseconds
    @Scheduled(fixedRate = 500)
    public void generateAndLogTraffic() {
        NetworkLog logEntry = createTrafficLog();
        log.info("Generated Log: {}", logEntry);
    }

    private NetworkLog createTrafficLog() {
        int chance = random.nextInt(100);
        Instant timestamp = Instant.now();
        String sourceIp = generateSourceIp();
        String destinationIp;
        String protocol = "TCP";
        int port;
        long bytesSent, bytesReceived, durationMs;

        if (chance < 60) {
            port = 443;
            destinationIp = generateDestinationIp(false);
            bytesSent = random.nextInt(5000);
            bytesReceived = random.nextInt(5000);
            durationMs = 10 + random.nextInt(500);
        } else if (chance < 94) {
            port = 80;
            destinationIp = generateDestinationIp(false);
            bytesSent = 1000 + random.nextInt(4000);
            bytesReceived = 1000 + random.nextInt(4000);
            durationMs = 10 + random.nextInt(500);
        } else if (chance < 97) {
            port = 4444;
            destinationIp = generateDestinationIp(true);
            bytesSent = 10000 + random.nextInt(5000);
            bytesReceived = 10000 + random.nextInt(5000);
            durationMs = 1000 + random.nextInt(500);
        } else {
            port = 1000 + random.nextInt(3000); // Fixed port generation math
            destinationIp = generateDestinationIp(true);
            bytesSent = 10000 + random.nextInt(5000);
            bytesReceived = 10000 + random.nextInt(5000);
            durationMs = 1000 + random.nextInt(500);
        }

        return new NetworkLog(timestamp, sourceIp, destinationIp, protocol, port, bytesSent, bytesReceived, durationMs);
    }

    private String generateSourceIp() {
        int chance = random.nextInt(100);
        // Assuming a hybrid, 70% work from office and 30% from home
        if (chance < 70) {
            return "172." + (16 + random.nextInt(16)) + "." + random.nextInt(256) + "." + random.nextInt(256);
        } else {
            return "192.168." + random.nextInt(256) + "." + random.nextInt(256);
        }
    }

    private String generateDestinationIp(boolean isAnomaly) {
        if (isAnomaly) {
            int firstOctet = random.nextInt(223) + 1;
            while (firstOctet == 10 || firstOctet == 172 || firstOctet == 192 || firstOctet == 127) {
                firstOctet = random.nextInt(223) + 1;
            }
            return firstOctet + "." + random.nextInt(256) + "." + random.nextInt(256) + "." + random.nextInt(256);
        } else {
            return NORMAL_DESTINATION_IPS[random.nextInt(NORMAL_DESTINATION_IPS.length)];
        }
    }
}