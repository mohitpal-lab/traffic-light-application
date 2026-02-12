package com.traffic.trafficlight.entity;

import com.traffic.trafficlight.model.Direction;
import com.traffic.trafficlight.model.LightColor;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrafficLightHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Direction direction;

    @Enumerated(EnumType.STRING)
    private LightColor oldState;

    @Enumerated(EnumType.STRING)
    private LightColor newState;

    private long timestamp;
}