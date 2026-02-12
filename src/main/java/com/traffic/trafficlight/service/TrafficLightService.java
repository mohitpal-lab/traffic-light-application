package com.traffic.trafficlight.service;

import com.traffic.trafficlight.entity.TrafficLightHistory;
import com.traffic.trafficlight.model.Direction;
import com.traffic.trafficlight.model.LightColor;
import com.traffic.trafficlight.model.TrafficPhase;
import com.traffic.trafficlight.repository.TrafficLightHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TrafficLightService {

    private final TrafficLightHistoryRepository trafficLightHistoryRepository;

    private TrafficPhase currentPhase = TrafficPhase.NS_LEFT_GREEN;
    private boolean paused = false;

    // Stores last known lights per direction for auditing
    private Map<Direction, LightColor> previousLights = new HashMap<>();

    public TrafficLightService(TrafficLightHistoryRepository repository) {
        this.trafficLightHistoryRepository = repository;

        previousLights = computeLightsForPhase(currentPhase);
    }

    private Map<Direction, LightColor> computeLightsForPhase(TrafficPhase phase) {
        Map<Direction, LightColor> map = new HashMap<>();

        switch (phase) {

            case NS_LEFT_GREEN:
                map.put(Direction.NS_LEFT, LightColor.GREEN);
                map.put(Direction.NS_STRAIGHT, LightColor.RED);
                map.put(Direction.EW_LEFT, LightColor.RED);
                map.put(Direction.EW_STRAIGHT, LightColor.RED);
                break;

            case NS_LEFT_YELLOW:
                map.put(Direction.NS_LEFT, LightColor.YELLOW);
                map.put(Direction.NS_STRAIGHT, LightColor.RED);
                map.put(Direction.EW_LEFT, LightColor.RED);
                map.put(Direction.EW_STRAIGHT, LightColor.RED);
                break;

            case NS_STRAIGHT_PERMISSIVE:
                map.put(Direction.NS_LEFT, LightColor.GREEN);
                map.put(Direction.NS_STRAIGHT, LightColor.GREEN);
                map.put(Direction.EW_LEFT, LightColor.RED);
                map.put(Direction.EW_STRAIGHT, LightColor.RED);
                break;

            case NS_STRAIGHT_YELLOW:
                map.put(Direction.NS_LEFT, LightColor.YELLOW);
                map.put(Direction.NS_STRAIGHT, LightColor.YELLOW);
                map.put(Direction.EW_LEFT, LightColor.RED);
                map.put(Direction.EW_STRAIGHT, LightColor.RED);
                break;

            case EW_LEFT_GREEN:
                map.put(Direction.NS_LEFT, LightColor.RED);
                map.put(Direction.NS_STRAIGHT, LightColor.RED);
                map.put(Direction.EW_LEFT, LightColor.GREEN);
                map.put(Direction.EW_STRAIGHT, LightColor.RED);
                break;

            case EW_LEFT_YELLOW:
                map.put(Direction.NS_LEFT, LightColor.RED);
                map.put(Direction.NS_STRAIGHT, LightColor.RED);
                map.put(Direction.EW_LEFT, LightColor.YELLOW);
                map.put(Direction.EW_STRAIGHT, LightColor.RED);
                break;

            case EW_STRAIGHT_PERMISSIVE:
                map.put(Direction.NS_LEFT, LightColor.RED);
                map.put(Direction.NS_STRAIGHT, LightColor.RED);
                map.put(Direction.EW_LEFT, LightColor.GREEN);
                map.put(Direction.EW_STRAIGHT, LightColor.GREEN);
                break;

            case EW_STRAIGHT_YELLOW:
                map.put(Direction.NS_LEFT, LightColor.RED);
                map.put(Direction.NS_STRAIGHT, LightColor.RED);
                map.put(Direction.EW_LEFT, LightColor.YELLOW);
                map.put(Direction.EW_STRAIGHT, LightColor.YELLOW);
                break;
        }

        return map;
    }

    public synchronized void nextSequence() {
        if (paused) return;

        switch (currentPhase) {
            case NS_LEFT_GREEN: currentPhase = TrafficPhase.NS_LEFT_YELLOW; break;
            case NS_LEFT_YELLOW: currentPhase = TrafficPhase.NS_STRAIGHT_PERMISSIVE; break;
            case NS_STRAIGHT_PERMISSIVE: currentPhase = TrafficPhase.NS_STRAIGHT_YELLOW; break;
            case NS_STRAIGHT_YELLOW: currentPhase = TrafficPhase.EW_LEFT_GREEN; break;
            case EW_LEFT_GREEN: currentPhase = TrafficPhase.EW_LEFT_YELLOW; break;
            case EW_LEFT_YELLOW: currentPhase = TrafficPhase.EW_STRAIGHT_PERMISSIVE; break;
            case EW_STRAIGHT_PERMISSIVE: currentPhase = TrafficPhase.EW_STRAIGHT_YELLOW; break;
            case EW_STRAIGHT_YELLOW: currentPhase = TrafficPhase.NS_LEFT_GREEN; break;
        }

        Map<Direction, LightColor> newLights = computeLightsForPhase(currentPhase);

        for (Direction dir : newLights.keySet()) {
            LightColor oldColor = previousLights.get(dir);
            LightColor newColor = newLights.get(dir);

            if (oldColor != newColor) {
                trafficLightHistoryRepository.save(
                        new TrafficLightHistory(
                                null,
                                dir,
                                oldColor,
                                newColor,
                                System.currentTimeMillis()
                        )
                );
            }
        }

        previousLights = newLights;
    }

    public synchronized void pause() {
        paused = true;
    }

    public synchronized void resume() {
        paused = false;
    }

    public Map<Direction, LightColor> getCurrentLightColors() {
        return computeLightsForPhase(currentPhase);
    }

    public TrafficPhase getCurrentPhase() {
        return currentPhase;
    }

    public List<TrafficLightHistory> getHistory() {
        return trafficLightHistoryRepository.findAll();
    }
}
