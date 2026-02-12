package com.traffic.trafficlight.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrafficLightState {
    private Direction direction;
    private LightColor color;
    private long timestamp;
}
