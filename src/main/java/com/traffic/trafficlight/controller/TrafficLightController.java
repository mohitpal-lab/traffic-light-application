package com.traffic.trafficlight.controller;

import com.traffic.trafficlight.entity.TrafficLightHistory;
import com.traffic.trafficlight.model.CurrentStateDTO;
import com.traffic.trafficlight.model.Direction;
import com.traffic.trafficlight.model.LightColor;
import com.traffic.trafficlight.service.TrafficLightService;
import org.springframework.http.ResponseEntity;
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
    public Map<Direction, LightColor> getState() {
        return service.getCurrentLightColors();
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
        try {
            service.resume();
        }catch(Exception ex){

        }
        return "System resumed";
    }

    @GetMapping("/history")
    public List<TrafficLightHistory> history() {
        return service.getHistory();
    }


    @PostMapping("/sequence/skip/{steps}")
    public ResponseEntity<CurrentStateDTO> skipSignals(@PathVariable int steps) {

        return ResponseEntity.ok(service.skipSignals(steps));
    }

    @GetMapping("/timing")
    public Map<String, Integer> getTiming() {
        return service.getTiming();
    }


}
