package de.peaqe.revitalizecore.logging;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 29.11.2025 | 15:52 Uhr
 * *
 */

public record LogEntry(
        long timestampMillis,
        String module,
        LogLevel level,
        String message
) {
}
