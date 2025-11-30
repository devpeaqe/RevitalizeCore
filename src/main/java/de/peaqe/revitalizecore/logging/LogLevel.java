package de.peaqe.revitalizecore.logging;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 29.11.2025 | 15:52 Uhr
 * *
 */

public enum LogLevel {

    INFO,
    WARN,
    ERROR,
    DEBUG;

    public static LogLevel fromString(String input) {
        for (LogLevel value : values()) {
            if (value.name().equalsIgnoreCase(input)) {
                return value;
            }
        }
        return null;
    }

}
