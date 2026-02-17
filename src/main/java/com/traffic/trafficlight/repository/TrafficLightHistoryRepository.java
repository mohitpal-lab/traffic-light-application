package com.traffic.trafficlight.repository;

import com.traffic.trafficlight.entity.TrafficLightHistory;
import java.util.List;

/**
 * Same name as before. No longer extends JpaRepository and will read and write from file.
 */
public interface TrafficLightHistoryRepository {

    TrafficLightHistory save(TrafficLightHistory record);

    List<TrafficLightHistory> findAll();
}
