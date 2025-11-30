package de.peaqe.revitalizecore.modules.player.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.peaqe.revitalizecore.cache.CacheRepository;
import de.peaqe.revitalizecore.database.HikariDatabaseProvider;
import de.peaqe.revitalizecore.modules.player.PlayerModule;
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

    private final PlayerModule playerModule;
    private final Gson gson;

    public PlayerRepository(PlayerModule playerModule) {
        this.playerModule = playerModule;
        this.gson = new GsonBuilder().create();
    }

    @Override
    public void setupTable(HikariDatabaseProvider db) {

        this.playerModule.getLogger().info("Setting up table 'players'...");

        db.update("""
            CREATE TABLE IF NOT EXISTS players (
                uuid VARCHAR(36) PRIMARY KEY,
                name VARCHAR(16) NOT NULL,
                data JSON NOT NULL
            )
        """);

        this.playerModule.getLogger().info("Table 'players' ensured to exist.");
    }

    @Override
    public PlayerObject load(String uuid, HikariDatabaseProvider db) {

        this.playerModule.getLogger().debug("Loading PlayerObject by UUID: " + uuid);

        var result = db.query(
                "SELECT data FROM players WHERE uuid=?",
                rs -> {
                    try {
                        if (!rs.next()) {
                            this.playerModule.getLogger().debug("No player found with UUID: " + uuid);
                            return null;
                        }
                        var json = rs.getString("data");
                        this.playerModule.getLogger().debug("PlayerObject JSON loaded for UUID: " + uuid);
                        return gson.fromJson(json, PlayerObject.class);
                    } catch (SQLException e) {
                        this.playerModule.getLogger().error("Error reading SQL result for UUID: " + uuid);
                        throw new RuntimeException(e);
                    }
                },
                uuid
        );

        this.playerModule.getLogger().debug("Finished loading PlayerObject for UUID: " + uuid);
        return result;
    }

    public PlayerObject loadByName(String name, HikariDatabaseProvider db) {

        this.playerModule.getLogger().debug("Loading PlayerObject by name: " + name);

        var result = db.query(
                "SELECT data FROM players WHERE name=?",
                rs -> {
                    try {
                        if (!rs.next()) {
                            this.playerModule.getLogger().debug("No player found with name: " + name);
                            return null;
                        }
                        var json = rs.getString("data");
                        this.playerModule.getLogger().debug("PlayerObject JSON loaded for name: " + name);
                        return gson.fromJson(json, PlayerObject.class);
                    } catch (SQLException e) {
                        this.playerModule.getLogger().error("Error reading SQL result for name: " + name);
                        throw new RuntimeException(e);
                    }
                },
                name
        );

        this.playerModule.getLogger().debug("Finished loading PlayerObject for name: " + name);
        return result;
    }

    @Override
    public void save(String uuid, PlayerObject obj, HikariDatabaseProvider db) {

        this.playerModule.getLogger().debug("Saving PlayerObject: UUID=" + uuid + ", Name=" + obj.getName());

        var json = gson.toJson(obj);
        this.playerModule.getLogger().debug("Serialized PlayerObject for UUID=" + uuid);

        db.update("""
            REPLACE INTO players (uuid, name, data)
            VALUES (?, ?, ?)
        """,
                uuid, obj.getName(), json
        );

        this.playerModule.getLogger().info("PlayerObject saved: UUID=" + uuid + " Name=" + obj.getName());
    }

}
