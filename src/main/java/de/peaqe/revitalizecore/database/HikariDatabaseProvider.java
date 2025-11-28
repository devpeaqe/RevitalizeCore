package de.peaqe.revitalizecore.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 28.11.2025 | 15:23 Uhr
 * *
 */

public class HikariDatabaseProvider {

    private final HikariDataSource dataSource;

    public HikariDatabaseProvider(String host,
                                  int port,
                                  String database,
                                  String user,
                                  String password) {

        var cfg = new HikariConfig();

        cfg.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useSSL=false&characterEncoding=utf8&autoReconnect=true");
        cfg.setUsername(user);
        cfg.setPassword(password);

        cfg.setMaximumPoolSize(10);
        cfg.setMinimumIdle(2);
        cfg.setConnectionTimeout(5000);
        cfg.setIdleTimeout(60000);
        cfg.setMaxLifetime(1800000);
        cfg.setPoolName("RevitalizePool");

        this.dataSource = new HikariDataSource(cfg);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        dataSource.close();
    }

    public <T> T query(String sql, Function<ResultSet, T> mapper, Object... params) {
        try (var con = getConnection();
             var stmt = prepare(con, sql, params);
             var rs = stmt.executeQuery()) {

            return mapper.apply(rs);

        } catch (SQLException e) {
            throw new RuntimeException("SQL Query Error: " + sql, e);
        }
    }

    public int update(String sql, Object... params) {
        try (var con = getConnection();
             var stmt = prepare(con, sql, params)) {

            return stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("SQL Update Error: " + sql, e);
        }
    }

    private PreparedStatement prepare(Connection con, String sql, Object... params) throws SQLException {
        var stmt = con.prepareStatement(sql);

        for (int i = 0; i < params.length; i++)
            stmt.setObject(i + 1, params[i]);

        return stmt;
    }

}
