package com.traffic.trafficlight.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.traffic.trafficlight.entity.TrafficLightHistory;
import org.springframework.stereotype.Repository;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository // Spring bean with same role as the earlier JPA repository
public class TrafficLightHistoryRepositoryImpl implements TrafficLightHistoryRepository {

    private final Path directory;
    private final ObjectMapper mapper;
    private BufferedWriter writer;
    private LocalDate currentDate = LocalDate.now();

    // Optional local ID generator if your code expects IDs to be non-null
    private final AtomicLong idSeq = new AtomicLong(1);

    public TrafficLightHistoryRepositoryImpl() {
        this.directory = Paths.get("data/history"); // adjust or externalize via properties
        this.mapper = new ObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        initWriter();
    }

    private void initWriter() {
        try {
            Files.createDirectories(directory);
            Path file = resolveFileForDate(currentDate);
            writer = Files.newBufferedWriter(
                    file,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize history writer", e);
        }
    }

    private Path resolveFileForDate(LocalDate date) {
        String fileName = String.format("traffic_history_%s.json", date); // JSON Lines per day
        return directory.resolve(fileName);
    }

    private void rotateIfNeeded() throws IOException {
        LocalDate today = LocalDate.now();
        if (!today.equals(currentDate)) {
            writer.close();
            currentDate = today;
            initWriter();
        }
    }

    @Override
    public synchronized TrafficLightHistory save(TrafficLightHistory record) {
        try {
            rotateIfNeeded();

            // Ensure ID is present if your code relies on it
            if (record.getId() == null) {
                record.setId(idSeq.getAndIncrement());
            }

            String json = mapper.writeValueAsString(record);
            writer.write(json);
            writer.newLine();
            writer.flush();
            return record;
        } catch (IOException e) {
            throw new RuntimeException("Failed to write history record", e);
        }
    }

    @Override
    public synchronized List<TrafficLightHistory> findAll() {
        List<TrafficLightHistory> out = new ArrayList<>();
        try {
            // Read today's file only (simple start). You can extend to read all days.
            Path file = resolveFileForDate(currentDate);
            if (!Files.exists(file)) return out;

            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line == null || line.isBlank()) continue;
                TrafficLightHistory rec = mapper.readValue(line, TrafficLightHistory.class);
                out.add(rec);
            }
            return out;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read history records", e);
        }
    }
}
