package com.traffic.trafficlight.config;

import com.traffic.trafficlight.model.TrafficPhase;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConfigurationProperties(prefix = "traffic.phase")
public class PhaseTimingConfig {

    private final Map<String, Integer> value;

    public PhaseTimingConfig(Map<String, Integer> value) {
        this.value = value;
    }

    public Map<String, Integer> getTiming() {
        return value;
    }

    public int getDurationForPhase(TrafficPhase phase) {
        return value.getOrDefault(phase.name(), 10);
    }
}
