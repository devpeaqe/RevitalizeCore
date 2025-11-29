package de.peaqe.revitalizecore.modules.player.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.peaqe.revitalizecore.cache.CacheRepository;
import de.peaqe.revitalizecore.database.HikariDatabaseProvider;
import de.peaqe.revitalizecore.modules.player.objects.PlayerObject;

import java.sql.SQLException;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 28.11.2025 | 15:52 Uhr
 * *
 */

public class PlayerRepository extends CacheRepository<PlayerObject> {

    private final Gson gson;

    public PlayerRepository() {
        this.gson = new GsonBuilder().create();
    }

    @Override
    public void setupTable(HikariDatabaseProvider db) {
        db.update("""
            CREATE TABLE IF NOT EXISTS players (
                uuid VARCHAR(36) PRIMARY KEY,
                name VARCHAR(16) NOT NULL,
                data JSON NOT NULL
            )
        """);
    }

    @Override
    public PlayerObject load(String uuid, HikariDatabaseProvider db) {

        return db.query(
                "SELECT data FROM players WHERE uuid=?",
                rs -> {
                    try {
                        if (!rs.next()) return null;
                        var json = rs.getString("data");
                        return gson.fromJson(json, PlayerObject.class);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                uuid
        );
    }

    public PlayerObject loadByName(String name, HikariDatabaseProvider db) {

        return db.query(
                "SELECT data FROM players WHERE name=?",
                rs -> {
                    try {
                        if (!rs.next()) return null;
                        var json = rs.getString("data");
                        return gson.fromJson(json, PlayerObject.class);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                name
        );
    }

    @Override
    public void save(String uuid, PlayerObject obj, HikariDatabaseProvider db) {

        var json = gson.toJson(obj);

        db.update("""
            REPLACE INTO players (uuid, name, data)
            VALUES (?, ?, ?)
        """,
                uuid, obj.getName(), json);
    }

}
