package de.peaqe.revitalizecore.cache;

import lombok.Getter;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 28.11.2025 | 15:14 Uhr
 * *
 */

@Getter
public class CacheEntry<T> {

    private T value;
    private boolean dirty;

    public CacheEntry(T value) {
        this.value = value;
        this.dirty = false;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
        this.dirty = true;
    }

    public void markClean() {
        this.dirty = false;
    }
}
