package com.traffic.trafficlight.controller;

import com.traffic.trafficlight.entity.TrafficLightHistory;
import com.traffic.trafficlight.model.Direction;
import com.traffic.trafficlight.model.TrafficLightState;
import com.traffic.trafficlight.service.TrafficLightService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/traffic")
public class TrafficLightController {

    private final TrafficLightService service;

    public TrafficLightController(TrafficLightService service) {
        this.service = service;
    }

    @GetMapping("/state")
    public Map<Direction, TrafficLightState> getState() {
        return service.getState();
    }

    @PostMapping("/sequence/next")
    public String next() {
        service.nextSequence();
        return "Sequence updated";
    }

    @PostMapping("/pause")
    public String pause() {
        service.pause();
        return "System paused";
    }

    @PostMapping("/resume")
    public String resume() {
        service.resume();
        return "System resumed";
    }

    @GetMapping("/history")
    public List<TrafficLightHistory> history() {
        return service.getHistory();
    }
}
