package com.traffic.trafficlight.service;

import com.traffic.trafficlight.model.Direction;
import com.traffic.trafficlight.model.LightColor;
import com.traffic.trafficlight.model.TrafficPhase;
import com.traffic.trafficlight.repository.TrafficLightHistoryRepository;

import com.traffic.trafficlight.repository.TrafficLightHistoryRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TrafficLightServiceTest {

    private TrafficLightHistoryRepositoryImpl repository;
    private TrafficLightService service;

    @BeforeEach
    void setup() {
        repository = Mockito.mock(TrafficLightHistoryRepositoryImpl.class);
        service   = new TrafficLightService(repository);
    }

    // Helper assertion to reduce repeated code
    private void assertColors(Map<Direction, LightColor> actual,
                              LightColor nsLeft,
                              LightColor nsStraight,
                              LightColor ewLeft,
                              LightColor ewStraight) {
        assertEquals(nsLeft, actual.get(Direction.NS_LEFT));
        assertEquals(nsStraight, actual.get(Direction.NS_STRAIGHT));
        assertEquals(ewLeft, actual.get(Direction.EW_LEFT));
        assertEquals(ewStraight, actual.get(Direction.EW_STRAIGHT));
    }

    // TEST 1: NS_LEFT_GREEN — Starting phase
    // Meaning: Only North–South left-turn cars can go.
    @Test
    void testPhase_NS_LEFT_GREEN() {

        assertEquals(TrafficPhase.NS_LEFT_GREEN, service.getCurrentPhase(),
                "Initial phase must start at NS_LEFT_GREEN");

        Map<Direction, LightColor> lights = service.getCurrentLightColors();

        assertColors(lights,
                LightColor.GREEN,  // NS left turn allowed
                LightColor.RED,    // NS straight must stop
                LightColor.RED,    // EW left must stop
                LightColor.RED     // EW straight must stop
        );
    }

    // TEST 2: NS_LEFT_YELLOW
    // Meaning: NS left-turn cars are finishing and should slow/stop soon.
    @Test
    void testPhase_NS_LEFT_YELLOW() {

        service.nextSequence();  // Move 1 step ahead

        assertEquals(TrafficPhase.NS_LEFT_YELLOW, service.getCurrentPhase(),
                "Expected phase after first transition");

        Map<Direction, LightColor> lights = service.getCurrentLightColors();

        assertColors(lights,
                LightColor.YELLOW, // NS left-turn finishing
                LightColor.RED,
                LightColor.RED,
                LightColor.RED
        );
    }

    // TEST 3: NS_STRAIGHT_PERMISSIVE
    // Meaning: NS straight traffic goes; left turn allowed when safe.
    @Test
    void testPhase_NS_STRAIGHT_PERMISSIVE() {

        service.nextSequence();
        service.nextSequence();

        assertEquals(TrafficPhase.NS_STRAIGHT_PERMISSIVE, service.getCurrentPhase(),
                "Expected phase after two transitions");

        Map<Direction, LightColor> lights = service.getCurrentLightColors();

        assertColors(lights,
                LightColor.GREEN,  // NS left (permissive)
                LightColor.GREEN,  // NS straight
                LightColor.RED,
                LightColor.RED
        );
    }

    // TEST 4: NS_STRAIGHT_YELLOW
    // Meaning: NS straight cars slowing; preparing to stop.
    @Test
    void testPhase_NS_STRAIGHT_YELLOW() {

        service.nextSequence(); // NS_LEFT_YELLOW
        service.nextSequence(); // NS_STRAIGHT_PERMISSIVE
        service.nextSequence(); // NS_STRAIGHT_YELLOW

        assertEquals(TrafficPhase.NS_STRAIGHT_YELLOW, service.getCurrentPhase());

        Map<Direction, LightColor> lights = service.getCurrentLightColors();

        assertColors(lights,
                LightColor.YELLOW, // NS left slowing
                LightColor.YELLOW, // NS straight slowing
                LightColor.RED,
                LightColor.RED
        );
    }

    // TEST 5: EW_LEFT_GREEN
    // Meaning: East–West left-turn traffic can go.
    @Test
    void testPhase_EW_LEFT_GREEN() {

        for (int i = 0; i < 4; i++) service.nextSequence();

        assertEquals(TrafficPhase.EW_LEFT_GREEN, service.getCurrentPhase());

        Map<Direction, LightColor> lights = service.getCurrentLightColors();

        assertColors(lights,
                LightColor.RED,
                LightColor.RED,
                LightColor.GREEN,  // EW left turn allowed
                LightColor.RED
        );
    }

    // TEST 6: EW_LEFT_YELLOW
    // Meaning: EW left-turn cars are finishing movement.
    @Test
    void testPhase_EW_LEFT_YELLOW() {

        for (int i = 0; i < 5; i++) service.nextSequence();

        assertEquals(TrafficPhase.EW_LEFT_YELLOW, service.getCurrentPhase());

        Map<Direction, LightColor> lights = service.getCurrentLightColors();

        assertColors(lights,
                LightColor.RED,
                LightColor.RED,
                LightColor.YELLOW, // EW left-turn finishing
                LightColor.RED
        );
    }

    // TEST 7: EW_STRAIGHT_PERMISSIVE
    // Meaning: EW straight allowed; left-turns allowed only when safe.
    @Test
    void testPhase_EW_STRAIGHT_PERMISSIVE() {

        for (int i = 0; i < 6; i++) service.nextSequence();

        assertEquals(TrafficPhase.EW_STRAIGHT_PERMISSIVE, service.getCurrentPhase());

        Map<Direction, LightColor> lights = service.getCurrentLightColors();

        assertColors(lights,
                LightColor.RED,
                LightColor.RED,
                LightColor.GREEN,  // EW left (permissive)
                LightColor.GREEN   // EW straight
        );
    }

    // TEST 8: EW_STRAIGHT_YELLOW
    // Meaning: EW straight traffic slowing; preparing to stop.
    @Test
    void testPhase_EW_STRAIGHT_YELLOW() {

        for (int i = 0; i < 7; i++) service.nextSequence();

        assertEquals(TrafficPhase.EW_STRAIGHT_YELLOW, service.getCurrentPhase());

        Map<Direction, LightColor> lights = service.getCurrentLightColors();

        assertColors(lights,
                LightColor.RED,
                LightColor.RED,
                LightColor.YELLOW, // EW left slowing
                LightColor.YELLOW  // EW straight slowing
        );
    }
}
