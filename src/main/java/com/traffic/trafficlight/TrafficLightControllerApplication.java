package com.traffic.trafficlight;

import com.traffic.trafficlight.config.PhaseTimingConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(PhaseTimingConfig.class)
@SpringBootApplication
public class TrafficLightControllerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrafficLightControllerApplication.class, args);
	}

}

