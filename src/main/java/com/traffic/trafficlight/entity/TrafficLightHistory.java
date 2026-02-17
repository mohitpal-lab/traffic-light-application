package com.traffic.trafficlight.entity;

import com.traffic.trafficlight.model.Direction;
import com.traffic.trafficlight.model.LightColor;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrafficLightHistory {


    private Long id;

    private Direction direction;

    private LightColor oldState;

    private LightColor newState;

    private long timestamp;
}
