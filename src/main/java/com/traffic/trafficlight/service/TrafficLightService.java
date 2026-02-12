package com.traffic.trafficlight.service;

import com.traffic.trafficlight.entity.TrafficLightHistory;
import com.traffic.trafficlight.model.*;
import com.traffic.trafficlight.repository.TrafficLightHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TrafficLightService {

    private final TrafficLightHistoryRepository trafficLightHistoryRepository;
    private final Map<Direction, TrafficLightState> state = new ConcurrentHashMap<>();
    private boolean paused = false;

    public TrafficLightService(TrafficLightHistoryRepository trafficLightHistoryRepository) {
        this.trafficLightHistoryRepository = trafficLightHistoryRepository;

        // Initial state
        state.put(Direction.NORTH_SOUTH,
                new TrafficLightState(Direction.NORTH_SOUTH, LightColor.GREEN, System.currentTimeMillis()));
        state.put(Direction.EAST_WEST,
                new TrafficLightState(Direction.EAST_WEST, LightColor.RED, System.currentTimeMillis()));
    }

    /** Audit a state change to the database. */
    private void audit(Direction direction, LightColor oldState, LightColor newState) {
        trafficLightHistoryRepository.save(new TrafficLightHistory(
                null, direction, oldState, newState, System.currentTimeMillis()
        ));
    }

    /** Advance the sequence one step. */
    public synchronized void nextSequence() {
        if (paused) return;

        TrafficLightState ns = state.get(Direction.NORTH_SOUTH);
        TrafficLightState ew = state.get(Direction.EAST_WEST);

        if (ns.getColor() == LightColor.GREEN) {
            audit(Direction.NORTH_SOUTH, LightColor.GREEN, LightColor.YELLOW);
            ns.setColor(LightColor.YELLOW);

        } else if (ns.getColor() == LightColor.YELLOW) {
            audit(Direction.NORTH_SOUTH, LightColor.YELLOW, LightColor.RED);
            audit(Direction.EAST_WEST, LightColor.RED, LightColor.GREEN);
            ns.setColor(LightColor.RED);
            ew.setColor(LightColor.GREEN);

        } else if (ew.getColor() == LightColor.GREEN) {
            audit(Direction.EAST_WEST, LightColor.GREEN, LightColor.YELLOW);
            ew.setColor(LightColor.YELLOW);

        } else if (ew.getColor() == LightColor.YELLOW) {
            audit(Direction.EAST_WEST, LightColor.YELLOW, LightColor.RED);
            audit(Direction.NORTH_SOUTH, LightColor.RED, LightColor.GREEN);
            ew.setColor(LightColor.RED);
            ns.setColor(LightColor.GREEN);
        }
    }

    /** Pause both directions (all RED). */
    public synchronized void pause() {
        paused = true;

        state.forEach((dir, st) -> {
            if (st.getColor() != LightColor.RED) {
                audit(dir, st.getColor(), LightColor.RED);
                st.setColor(LightColor.RED);
            }
        });
    }

    /** Resume: NS=GREEN, EW=RED */
    public synchronized void resume() {
        paused = false;

        TrafficLightState ns = state.get(Direction.NORTH_SOUTH);
        TrafficLightState ew = state.get(Direction.EAST_WEST);

        if (ns.getColor() != LightColor.GREEN) {
            audit(Direction.NORTH_SOUTH, ns.getColor(), LightColor.GREEN);
            ns.setColor(LightColor.GREEN);
        }

        if (ew.getColor() != LightColor.RED) {
            audit(Direction.EAST_WEST, ew.getColor(), LightColor.RED);
            ew.setColor(LightColor.RED);
        }
    }

    /** Current state. */
    public Map<Direction, TrafficLightState> getState() {
        return state;
    }

    /** Audit history from DB. */
    public List<TrafficLightHistory> getHistory() {
        return trafficLightHistoryRepository.findAll();
    }
}
