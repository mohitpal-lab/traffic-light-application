package com.traffic.trafficlight.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentStateDTO {

    private  TrafficPhase trafficPhase;
    private Map<Direction,LightColor> colors;
    private int skipped;
}
