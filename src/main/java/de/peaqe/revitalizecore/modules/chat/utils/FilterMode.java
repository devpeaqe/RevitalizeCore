package de.peaqe.revitalizecore.modules.chat.utils;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 26.11.2025 | 14:43 Uhr
 * *
 */

public enum FilterMode {

    REPLACE,
    BLOCK;

    public static FilterMode fromString(String string) {
        return FilterMode.valueOf(string.toUpperCase());
    }

}
