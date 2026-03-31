package com.satya.network_anomaly_detection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NetworkAnomalyDetectionApplication {

	public static void main(String[] args) {
		SpringApplication.run(NetworkAnomalyDetectionApplication.class, args);
	}

}
