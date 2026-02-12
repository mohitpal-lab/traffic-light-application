package com.traffic.trafficlight.repository;

import com.traffic.trafficlight.entity.TrafficLightHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrafficLightHistoryRepository extends JpaRepository<TrafficLightHistory, Long> {
}
