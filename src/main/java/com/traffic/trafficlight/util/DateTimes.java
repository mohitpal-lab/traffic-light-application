package com.traffic.trafficlight.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Minimal UTC-focused date-time utilities for payment systems.
 * - Use UTC for storage, computation, and signatures.
 * - Expose RFC 3339 / ISO-8601 strings for APIs and logs.
 * - Support deterministic testing via Clock injection.
 */
public final class DateTimes {

    private DateTimes() {}

    /** RFC 3339 with milliseconds and offset, e.g. 2026-03-01T12:34:56.789Z or +05:30 */
    public static final DateTimeFormatter RFC3339_MILLIS =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    /** ISO instant with 'Z' (UTC), e.g. 2026-03-01T12:34:56.789Z */
    public static final DateTimeFormatter ISO_INSTANT_MILLIS = DateTimeFormatter.ISO_INSTANT;

    /** Always use UTC for storage and cryptographic material. */
    public static final ZoneId UTC = ZoneOffset.UTC;

    // -----------------------
    // "Now" — UTC, testable
    // -----------------------

    /** Instant now in UTC (uses provided clock; inject Clock.systemUTC() in prod). */
    public static Instant nowUtc(Clock clock) {
        return Instant.now(Objects.requireNonNull(clock, "clock"));
    }

    /** Epoch millis now in UTC. */
    public static long nowEpochMillis(Clock clock) {
        return nowUtc(clock).toEpochMilli();
    }

    /** Epoch millis → Instant (UTC). */
    public static Instant toInstant(long epochMillis) {
        return Instant.ofEpochMilli(epochMillis);
    }

    /** Instant → epoch millis. */
    public static long toEpochMillis(Instant instant) {
        return Objects.requireNonNull(instant, "instant").toEpochMilli();
    }

    /** Instant → RFC3339 string with zone offset, e.g. 2026-03-01T18:04:05.123+05:30. */
    public static String formatRfc3339(Instant instant, ZoneId zone) {
        return RFC3339_MILLIS.format(Objects.requireNonNull(instant, "instant").atZone(Objects.requireNonNull(zone, "zone")));
    }

    /** Instant → ISO 8601 with 'Z' (UTC), e.g. 2026-03-01T12:34:56.789Z. */
    public static String formatIsoInstant(Instant instant) {
        return ISO_INSTANT_MILLIS.format(Objects.requireNonNull(instant, "instant"));
    }

    /** Epoch millis → ISO 8601 with 'Z' (UTC). */
    public static String formatIsoInstant(long epochMillis) {
        return formatIsoInstant(toInstant(epochMillis));
    }

    /**
     * Parse RFC 3339 / ISO 8601 strings that include zone/offset (e.g., 2026-03-01T18:04:05.123+05:30 or ...Z) to Instant.
     * Throws DateTimeParseException if invalid.
     */
    public static Instant parseRfc3339ToInstant(String iso) {
        return OffsetDateTime.parse(Objects.requireNonNull(iso, "iso")).toInstant();
    }

    /** Safe parse variant: returns null instead of throwing when invalid. */
    public static Instant tryParseRfc3339ToInstant(String iso) {
        try {
            return parseRfc3339ToInstant(iso);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }
}
