package de.peaqe.revitalizecore.database;

import de.peaqe.revitalizecore.cache.CacheRepository;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 28.11.2025 | 15:14 Uhr
 * *
 */

@Getter
public class DatabaseManager {

    private final HikariDatabaseProvider provider;
    private final Map<String, CacheRepository<?>> repositories = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(2);

    public DatabaseManager(HikariDatabaseProvider provider) {
        this.provider = provider;
    }

    public <T> void registerRepository(String id, CacheRepository<T> repo) {
        repositories.put(id, repo);
    }

    public void setupAllTables() {
        repositories.values().forEach(repo -> {
            repo.setupTable(provider);
        });
    }

    public void startAutoRefresh(long intervalSeconds) {
        scheduler.scheduleAtFixedRate(() ->
                repositories.values().forEach(repo ->
                        repo.refreshAsync(provider)
                ), intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
    }

    public void shutdown() {
        scheduler.shutdownNow();
        provider.close();
    }

}
