package de.peaqe.revitalizecore.cache;

import de.peaqe.revitalizecore.database.HikariDatabaseProvider;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 28.11.2025 | 15:14 Uhr
 * *
 */

public abstract class CacheRepository<T> {

    protected final ConcurrentHashMap<String, CacheEntry<T>> cache =
            new ConcurrentHashMap<>();

    public abstract T load(String key, HikariDatabaseProvider db);
    public abstract void save(String key, T obj, HikariDatabaseProvider db);

    public T get(String key, HikariDatabaseProvider db) {
        return cache.computeIfAbsent(key, k ->
                new CacheEntry<>(load(k, db))
        ).get();
    }

    public void update(String key, T value) {
        cache.computeIfAbsent(key, k -> new CacheEntry<>(value)).set(value);
    }

    public void refreshAsync(HikariDatabaseProvider db) {
        cache.forEach((key, entry) ->
                CompletableFuture.runAsync(() -> {
                    T newValue = load(key, db);
                    entry.set(newValue);
                })
        );
    }

    public abstract void setupTable(HikariDatabaseProvider db);

}

